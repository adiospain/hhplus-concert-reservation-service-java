package io.hhplus.concert_reservation_service_java.integration.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PointUseCaseConcurrencyTest {

  @Autowired
  private ChargePointUseCase chargePointUseCase;

  @Autowired
  private GetPointUseCase getPointUseCase;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User(1L, 1000);
    user = userRepository.save(user);
  }

  @Test
  @DisplayName("포인트 충전과 조회 동시성 테스트")
  void concurrentChargeAndGetPoint() throws InterruptedException {
    int numberOfThreads = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          if (index % 2 == 0) {
            // 짝수 인덱스: 포인트 충전
            ChargePointCommand chargeCommand = ChargePointCommand.builder()
                .userId(user.getId())
                .amount(100)
                .build();
            int result = chargePointUseCase.execute(chargeCommand);
            if (result > 0) {
              successfulOperations.incrementAndGet();
            }
          } else {
            // 홀수 인덱스: 포인트 조회
            GetPointCommand getCommand = GetPointCommand.builder()
                .reserverId(user.getId())
                .build();
            int point = getPointUseCase.execute(getCommand);
            if (point >= 0) {
              successfulOperations.incrementAndGet();
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(1, TimeUnit.MINUTES);
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    // 최종 포인트 확인
    User updatedUser = userService.getUserWithLock(user.getId());
    int point = userService.getPoint(user.getId());
    int expectedCharges = numberOfThreads / 2; // 충전 횟수
    int expectedFinalPoint = 1000 + (100 * expectedCharges); // 초기 포인트 + (충전 금액 * 충전 횟수)

    assertThat(point).isEqualTo(expectedFinalPoint);
    assertThat(successfulOperations.get()).isEqualTo(numberOfThreads);
  }
}