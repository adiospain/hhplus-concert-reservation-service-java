package io.hhplus.concert_reservation_service_java.integration.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ChargePointUseCaseIntegrationTest {

  @Autowired
  private ChargePointUseCase chargePointUseCase;


  @Autowired
  private UserRepository userRepository;

  @PersistenceContext
  private EntityManager entityManager;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User(1L, 1000);
    user = userRepository.save(user);
  }



  @Test
  void execute_ShouldChargePointsAndReturnUpdatedBalance_Concurrent() throws InterruptedException {
    // Given
    ChargePointCommand command = ChargePointCommand.builder()
        .userId(user.getId())
        .amount(500)
        .build();

    int numberOfThreads = 2000;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger successfulCharges = new AtomicInteger(0);

    // When
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.submit(() -> {
        try {
          int result = chargePointUseCase.execute(command);
          if (result > 0) {
            successfulCharges.incrementAndGet();
          }
        } catch (Exception e) {
          // Log the exception or handle it as needed
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await(1, TimeUnit.MINUTES);
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    // Then
    User updatedUser = userRepository.findByIdWithPessimisticLock(user.getId()).orElseThrow();
    assertThat(updatedUser.getPoint()).isEqualTo(user.getPoint() + 500 * numberOfThreads);
  }

  @Test
  void execute_ShouldChargePointsAndReturnUpdatedBalance_just_Many()  {
    // Given
    ChargePointCommand command = ChargePointCommand.builder()
        .userId(user.getId())
        .amount(500)
        .build();

    int numberOfThreads = 4000;

    // When
    for (int i = 0; i < numberOfThreads; i++) {
      //executorService.submit(() ->
          int result = chargePointUseCase.execute(command);
    }

    // Then
    User updatedUser = userRepository.findByIdWithPessimisticLock(user.getId()).orElseThrow();
    assertThat(updatedUser.getPoint()).isEqualTo(user.getPoint() + 500 * numberOfThreads);
  }


  @Test
  void execute_ShouldChargePointsAndReturnUpdatedBalance() {
    // Given
    ChargePointCommand command = ChargePointCommand.builder()
        .userId(user.getId())
        .amount(500)
        .build();

    // When
    int result = chargePointUseCase.execute(command);

    // Then
    assertThat(result).isEqualTo(user.getPoint()+500);
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getPoint()).isEqualTo(user.getPoint()+500);
  }

  @Test
  void execute_WhenUserNotFound_ShouldThrowException() {
    // Given
    ChargePointCommand command = ChargePointCommand.builder()
        .userId(999L)
        .amount(500)
        .build();

    // When & Then
    assertThatThrownBy(() -> chargePointUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
  }

  @Test
  void execute_WhenAmountIsNegative_ShouldThrowException() {
    // Given
    ChargePointCommand invalidCommand = ChargePointCommand.builder()
        .userId(user.getId())
        .amount(-500)
        .build();

    // When & Then
    assertThatThrownBy(() -> chargePointUseCase.execute(invalidCommand))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_AMOUNT);
  }

  @Test
  void execute_WhenAmountIsZero_ShouldThrowException() {
    // Given
    ChargePointCommand invalidCommand = ChargePointCommand.builder()
        .userId(user.getId())
        .amount(0)
        .build();

    // When & Then
    assertThatThrownBy(() -> chargePointUseCase.execute(invalidCommand))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_AMOUNT);
  }
}