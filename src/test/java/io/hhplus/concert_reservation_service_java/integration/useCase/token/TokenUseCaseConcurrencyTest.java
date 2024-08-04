package io.hhplus.concert_reservation_service_java.integration.useCase.token;

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
  private IssueTokenUseCase issueTokenUseCase;

  @Autowired
  private GetTokenUseCase getTokenUseCase;


  @Autowired
  private TokenRepository tokenRepository;

  @Test
  @DisplayName("여러 사용자(300명) 토큰 조회 동시성 테스트")
  void getToken_ConcurrencyTest() throws InterruptedException, ExecutionException {
    int threadCount = 300;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    ConcurrentHashMap<Long, AtomicInteger> queuePositions = new ConcurrentHashMap<>();

    long userId = 1L;



    List<Future<TokenDomain>> futures = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      String accessKey = "aa"+i;
      Token existingToken = Token.builder()
          .id(3L)
          .userId(userId+i)
          .accessKey(accessKey)
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

          System.out.println("Redis::getTokenUseCase execution time: " + durationMs + " ms");
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
        queuePositions.computeIfAbsent(result.getUserId(), k -> new AtomicInteger()).set((int) result.getQueuePosition());
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


    latch.await(5, TimeUnit.SECONDS);
    executorService.shutdown();

    //데이터 베이스 확인
    List<Token> allTokens = tokenRepository.findAll();
    List<Token> activeTokens = tokenRepository.findActiveTokens();
    List<Token> waitingTokens = tokenRepository.findWaitingTokens();

    int tokensCnt = allTokens.size();
    int activeCnt = activeTokens.size();
    int waitingCnt = waitingTokens.size();

    assertEquals(threadCount, activeCnt + waitingCnt);
    assertEquals(threadCount, tokensCnt);
  }
}
