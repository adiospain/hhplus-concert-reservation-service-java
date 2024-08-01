package io.hhplus.concert_reservation_service_java.integration.useCase.token;


import io.hhplus.concert_reservation_service_java.domain.token.ActivateNextTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenCommand;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.domain.user.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.IssueTokenCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenUseCaseConcurrencyTest {
  @Autowired
  private ActivateNextTokenUseCase activateNextTokenUseCase;

  @Autowired
  private IssueTokenUseCase issueTokenUseCase;

  @Autowired
  private GetTokenUseCase getTokenUseCase;


  @Autowired
  private TokenRepository tokenRepository;

  @Test
  @DisplayName("여러 사용자 토큰 조회 동시성 테스트")
  public void concurrentGetAndToken_manyUser() throws InterruptedException {
    int numberOfThreads =1000;
    User user = new User(1L, 1000);
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    AtomicInteger chargeFail = new AtomicInteger(0);
    AtomicInteger useFail = new AtomicInteger(0);
    AtomicInteger getFail = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      final long userId = user.getId() + i % 3;
      String accessKey = UUID.randomUUID().toString();
      IssueTokenCommand issueTokenCommand = IssueTokenCommand.builder()
          .accessKey(accessKey)
          .userId(userId)
          .build();

      GetTokenCommand getTokenCommand = GetTokenCommand.builder()
          .accessKey(accessKey)
          .userId(userId)
          .build();
      executorService.submit(() -> {
        try {
          {
            // 조회
            TokenDomain token = issueTokenUseCase.execute(issueTokenCommand);
            TokenDomain tokenDomain = getTokenUseCase.execute(getTokenCommand);
            if (tokenDomain != null ) {
              successfulOperations.incrementAndGet();
            }
          }
        } catch (Exception e) {
            getFail.incrementAndGet();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(1, TimeUnit.MINUTES);
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
  }

  @Test
  @DisplayName("여러 사용자(10명) 토큰 조회 동시성 테스트")
  void getToken_ConcurrencyTest() throws InterruptedException, ExecutionException {
    int threadCount = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    ConcurrentHashMap<Long, AtomicInteger> queuePositions = new ConcurrentHashMap<>();

    long userId = 1L;



    List<Future<TokenDomain>> futures = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      String accessKey = UUID.randomUUID().toString()+"aaa";
      Token existingToken = Token.builder()
          .userId(userId+i)
          .accessKey(accessKey)
          .status(TokenStatus.WAIT)
          .expireAt(LocalDateTime.now().plusMinutes(5))
          .build();
      tokenRepository.save(existingToken);


      GetTokenCommand getTokenCommand = GetTokenCommand.builder()
          .accessKey(existingToken.getAccessKey())
          .userId(existingToken.getUserId())
          .build();
      futures.add(executorService.submit(() -> {
        try {
          long startTime = System.nanoTime();
          TokenDomain result = getTokenUseCase.execute(getTokenCommand);
          long endTime = System.nanoTime();
          long duration = endTime - startTime;

          // Convert nanoseconds to milliseconds for readability
          double durationMs = duration / 1_000_000.0;

          System.out.println("DB::getTokenUseCase execution time: " + durationMs + " ms");
          return result;
        } finally {
          latch.countDown();
        }
      }));
    }

    latch.await(5, TimeUnit.SECONDS);
    executorService.shutdown();

    Map<Long, List<TokenDomain>> userTokens = new HashMap<>();
    AtomicInteger successfulRequests = new AtomicInteger(0);
    AtomicInteger failedRequests = new AtomicInteger(0);

    for (Future<TokenDomain> future : futures) {
      try{
        TokenDomain result = future.get();
        userTokens.computeIfAbsent(result.getUserId(), k -> new ArrayList<>()).add(result);
        queuePositions.computeIfAbsent(result.getQueuePosition(), k -> new AtomicInteger()).incrementAndGet();
        successfulRequests.incrementAndGet();
      } catch (ExecutionException e){
        if (e.getCause() instanceof CustomException) {
          failedRequests.incrementAndGet();
        } else {
          throw e; // 다른 예외는 다시 던짐
        }
        int a = 0;
      }

    }

    int activeTokens = queuePositions.getOrDefault(0L, new AtomicInteger()).get();
    int waitingTokens = queuePositions.getOrDefault(1L, new AtomicInteger()).get();

    assertEquals(threadCount, activeTokens);

    //토큰 상태확인
    for (List<TokenDomain> tokens : userTokens.values()) {
      boolean hasActiveToken = false;
      for (TokenDomain token : tokens) {
        if (token.getQueuePosition() == 0) {
          assertTrue(hasActiveToken == false, "Each user should have only one active token");
          hasActiveToken = true;
        }
      }
      assertTrue(hasActiveToken, "Each user should have at least one active token");
    }

    //데이터 베이스 확인
    List<Token> allTokens = tokenRepository.findAll();
    long activeTokenCount = allTokens.stream().filter(t -> t.getStatus() == TokenStatus.ACTIVE).count();
    assertEquals(10, activeTokenCount);
  }
}
