package io.hhplus.concert_reservation_service_java.integration.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
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
  private CreatePaymentUseCase createPaymentUseCase;

  @Autowired
  private GetPointUseCase getPointUseCase;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;


  @Test
  @DisplayName("포인트 충전과 조회 동시성 테스트")
  void concurrentChargeAndGetPoint() throws InterruptedException {
    int numberOfThreads = 7000;
    User user = new User(1L, 1000);
    userRepository.save(user);
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    AtomicInteger conccurentIssue = new AtomicInteger(0);
    AtomicInteger chargeFail = new AtomicInteger(0);
    AtomicInteger getFail = new AtomicInteger(0);
    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          if (index % 2 == 0) {
            // 짝수 인덱스: 포인트 충전
            ChargePointCommand chargeCommand = ChargePointCommand.builder()
                .userId(user.getId()+ (index%4))
                .amount(100)
                .build();
            int result = chargePointUseCase.execute(chargeCommand);
            if (result > 0) {
              successfulOperations.incrementAndGet();
            }
          } else {
            // 홀수 인덱스: 포인트 조회
            GetPointCommand getCommand = GetPointCommand.builder()
                .reserverId(user.getId()+ (index%4))
                .build();
            int point = getPointUseCase.execute(getCommand);
            if (point >= 0) {
              successfulOperations.incrementAndGet();
            }
          }
        } catch (CustomException e) {
          if (index % 2 == 0)
            chargeFail.incrementAndGet();
          else {
            getFail.incrementAndGet();
          }
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
    int expectedCharges = numberOfThreads / 2; // 충전 횟수
    int expectedFinalPoint = 1000 + (100 * (expectedCharges - chargeFail.get()));// 초기 포인트 + (충전 금액 * 충전 횟수)

    int totalOperations = successfulOperations.get() + chargeFail.get() + getFail.get();
    assertThat(totalOperations).isEqualTo(numberOfThreads);
  }

  @Test
  @DisplayName("포인트 충전, 사용, 조회 동시성 테스트")
  void concurrentChargeUseAndGetPoint() throws InterruptedException {
    int numberOfThreads = 3333;
    User user = new User(1L, 1000);
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    AtomicInteger chargeFail = new AtomicInteger(0);
    AtomicInteger useFail = new AtomicInteger(0);
    AtomicInteger getFail = new AtomicInteger(0);

    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          if (index % 3 == 0) {
            // 충전
            ChargePointCommand chargeCommand = ChargePointCommand.builder()
                .userId(user.getId())
                .amount(100)
                .build();
            int result = chargePointUseCase.execute(chargeCommand);
            if (result > 0) {
              successfulOperations.incrementAndGet();
            }
          } else if (index % 3 == 1) {
            // 사용
            createPaymentUseCase.usePoint(user.getId(), 50);
            successfulOperations.incrementAndGet();
          }
            else {
              // 조회
              GetPointCommand getCommand = GetPointCommand.builder()
                  .reserverId(user.getId())
                  .build();
              int point = getPointUseCase.execute(getCommand);
              if (point >= 0) {
                successfulOperations.incrementAndGet();
              }
            }
        } catch (Exception e) {
          if (index % 3 == 0)
            chargeFail.incrementAndGet();
          else if (index % 3 == 1)
            if (e instanceof CustomException){
              if (((CustomException)e).getErrorCode() == ErrorCode.NOT_ENOUGH_POINT){
                successfulOperations.incrementAndGet();
              }
              else {
                useFail.incrementAndGet();
              }
            }


          else
            getFail.incrementAndGet();
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
    int expectedCharges = numberOfThreads / 3; // 충전 횟수
    int expectedUses = numberOfThreads / 3; // 사용 횟수
    int expectedFinalPoint = 1000 + (100 * (expectedCharges - chargeFail.get())) - (50 * (expectedUses - useFail.get()));

    int totalOperations = successfulOperations.get() + chargeFail.get() + useFail.get() + getFail.get();
    assertThat(totalOperations).isEqualTo(numberOfThreads);
    assertThat(updatedUser.getPoint()).isEqualTo(expectedFinalPoint);

    // 추가적인 로그 출력
    System.out.println("Successful operations: " + successfulOperations.get());
    System.out.println("Charge failures: " + chargeFail.get());
    System.out.println("Use failures: " + useFail.get());
    System.out.println("Get failures: " + getFail.get());
    System.out.println("Final point: " + updatedUser.getPoint());
    System.out.println("Expected final point: " + expectedFinalPoint);
  }


  @Test
  @DisplayName("연속 포인트 충전, 사용, 조회 동시성 테스트")
  void concurrentChargeUseAndGetPoint_sequence() throws InterruptedException {
    int numberOfThreads = 3333;
    User user = new User(1L, 1000);
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    AtomicInteger chargeFail = new AtomicInteger(0);
    AtomicInteger useFail = new AtomicInteger(0);
    AtomicInteger getFail = new AtomicInteger(0);

    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          if (index % 3 == 0) {
            // 충전
            ChargePointCommand chargeCommand = ChargePointCommand.builder()
                .userId(user.getId())
                .amount(100)
                .build();
            chargePointUseCase.execute(chargeCommand);
            successfulOperations.incrementAndGet();
          }
          else if (index % 3==1){
              createPaymentUseCase.usePoint(user.getId(), 50);
              successfulOperations.incrementAndGet();
          } else {
            // 조회
            GetPointCommand getCommand = GetPointCommand.builder()
                .reserverId(user.getId())
                .build();
            int point = getPointUseCase.execute(getCommand);
            if (point >= 0) {
              successfulOperations.incrementAndGet();
            }
          }
        } catch (CustomException e) {
          if (index % 3 == 0)
            chargeFail.incrementAndGet();
          else if (index % 3 == 1)
            useFail.incrementAndGet();
          else
            getFail.incrementAndGet();
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
    int expectedCharges = numberOfThreads / 3; // 충전 횟수
    int expectedUses = numberOfThreads / 3; // 사용 횟수
    int expectedFinalPoint = 1000 + (100 * (expectedCharges - chargeFail.get())) - (50 * (expectedUses - useFail.get()));

    int totalOperations = successfulOperations.get() + chargeFail.get() + useFail.get() + getFail.get();
    assertThat(totalOperations).isEqualTo(numberOfThreads);
    assertThat(updatedUser.getPoint()).isEqualTo(expectedFinalPoint);

    // 추가적인 로그 출력
    System.out.println("Successful operations: " + successfulOperations.get());
    System.out.println("Charge failures: " + chargeFail.get());
    System.out.println("Use failures: " + useFail.get());
    System.out.println("Get failures: " + getFail.get());
    System.out.println("Final point: " + updatedUser.getPoint());
    System.out.println("Expected final point: " + expectedFinalPoint);
  }

  @Test
  @DisplayName("여러번 잔액 사용 동시성 테스트")
  void concurrentu_manyUse() throws InterruptedException {
    int initPoint = 1000;
    User user = new User(1L, initPoint);
    userRepository.save(user);
    int numberOfThreads = 3333;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    AtomicInteger notEnoughFail = new AtomicInteger(0);
    AtomicInteger dontCareFail = new AtomicInteger(0);

    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
            createPaymentUseCase.usePoint(user.getId(),1);
            successfulOperations.incrementAndGet();
        } catch (Exception e) {
          if (e instanceof CustomException){
            if (((CustomException) e).getErrorCode() == ErrorCode.NOT_ENOUGH_POINT){
              notEnoughFail.incrementAndGet();
            }
          }
          else {
            dontCareFail.incrementAndGet();
          }
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await(1, TimeUnit.MINUTES);
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
    int totalOperations = successfulOperations.get() + notEnoughFail.get() + dontCareFail.get();

    assertThat(totalOperations).isEqualTo(numberOfThreads);
    assertThat(notEnoughFail.get()).isEqualTo(numberOfThreads - initPoint);
    System.out.println("Successful operations: " + successfulOperations.get());
    System.out.println("Not Enough failures: " + notEnoughFail.get());
  }








  @Test
  @DisplayName("여러 사용자 포인트 충전, 사용, 조회 동시성 테스트")
  void concurrentChargeUseAndGetPoint_manyUser() throws InterruptedException {
    int numberOfThreads = 3333;
    User user = new User(1L, 1000);
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulOperations = new AtomicInteger(0);
    AtomicInteger chargeFail = new AtomicInteger(0);
    AtomicInteger useFail = new AtomicInteger(0);
    AtomicInteger getFail = new AtomicInteger(0);

    for (int i = 0; i < numberOfThreads; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          if (index % 3 == 0) {
            // 충전
            ChargePointCommand chargeCommand = ChargePointCommand.builder()
                .userId(user.getId()+4L)
                .amount(100)
                .build();
            int result = chargePointUseCase.execute(chargeCommand);
            if (result > 0) {
              successfulOperations.incrementAndGet();
            }
          } else if (index % 3 == 1) {
            // 사용
            createPaymentUseCase.usePoint(user.getId()+index%2,50);
            successfulOperations.incrementAndGet();
          }
          else {
            // 조회
            GetPointCommand getCommand = GetPointCommand.builder()
                .reserverId(user.getId() + index % 3)
                .build();
            int point = getPointUseCase.execute(getCommand);
            if (point >= 0) {
              successfulOperations.incrementAndGet();
            }
          }
        } catch (Exception e) {
          if (index % 3 == 0)
            chargeFail.incrementAndGet();
          else if (index % 3 == 1)
            useFail.incrementAndGet();
          else
            getFail.incrementAndGet();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(1, TimeUnit.MINUTES);
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    // 최종 포인트 확인
    User updatedUser = userService.getUserWithLock(user.getId()+4);
    int expectedCharges = numberOfThreads / 3; // 충전 횟수
    int expectedUses = numberOfThreads / 3; // 사용 횟수
    int expectedFinalPoint = 1500 + (100 * (expectedCharges - chargeFail.get()))
        //- (50 * (expectedUses - useFail.get()))
    ;

    int totalOperations = successfulOperations.get() + chargeFail.get() + useFail.get() + getFail.get();
    assertThat(totalOperations).isEqualTo(numberOfThreads);
    assertThat(updatedUser.getPoint()).isEqualTo(expectedFinalPoint);

    // 추가적인 로그 출력
    System.out.println("Successful operations: " + successfulOperations.get());
    System.out.println("Charge failures: " + chargeFail.get());
    System.out.println("Use failures: " + useFail.get());
    System.out.println("Get failures: " + getFail.get());
    System.out.println("Final point: " + updatedUser.getPoint());
    System.out.println("Expected final point: " + expectedFinalPoint);
  }
}