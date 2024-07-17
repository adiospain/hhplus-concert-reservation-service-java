package io.hhplus.concert_reservation_service_java.integration.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ChargePointUseCaseIntegrationTest {

  @Autowired
  private ChargePointUseCase chargePointUseCase;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User(1L, 500);
    user = userRepository.save(user);
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
    assertThat(result).isEqualTo(1000);
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getPoint()).isEqualTo(1000);
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