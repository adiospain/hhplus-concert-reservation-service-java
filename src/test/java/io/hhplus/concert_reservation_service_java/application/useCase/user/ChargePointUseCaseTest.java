package io.hhplus.concert_reservation_service_java.application.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.useCase.ChargePointUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChargePointUseCaseTest {
  private final UserService userService = Mockito.mock(UserService.class);
  private final ChargePointUseCase useCase = new ChargePointUseCaseImpl(userService);



  @BeforeEach
  void setUp() {

  }

  @Test
  void execute_ShouldChargePointsAndReturnUpdatedBalance() {
    User reserver = new User(1L, 500);
    ChargePointCommand command = ChargePointCommand.builder()
        .userId(1L)
        .amount(500)
        .build();
    // Given
    when(userService.chargePoint(command.getUserId(),command.getAmount()))
        .thenReturn(reserver);

    // When
    int result = useCase.execute(command);

    // Then
    assertThat(result).isEqualTo(500);
    verify(userService).chargePoint(1L, 500);
  }

  @Test
  void execute_WhenUserNotFound_ShouldThrowException() {
    // Given
    ChargePointCommand command = ChargePointCommand.builder()
        .userId(1L)
        .amount(500)
        .build();
    when(userService.chargePoint(command.getUserId(), command.getAmount())).thenThrow(new CustomException(ErrorCode.RESERVER_NOT_FOUND));

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVER_NOT_FOUND);
  }

  @Test
  void execute_WhenAmountIsNegative_ShouldThrowException() {
    // Given
    ChargePointCommand invalidCommand = ChargePointCommand.builder()
        .userId(1L)
        .amount(-500)
        .build();
    when(userService.chargePoint(invalidCommand.getUserId(), invalidCommand.getAmount())).thenThrow(new CustomException(ErrorCode.INVALID_AMOUNT));

    // When & Then
    assertThatThrownBy(() -> useCase.execute(invalidCommand))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_AMOUNT);
  }

  @Test
  void execute_WhenAmountIsZero_ShouldThrowException() {
    // Given
    ChargePointCommand invalidCommand = ChargePointCommand.builder()
        .userId(1L)
        .amount(0)
        .build();
    when(userService.chargePoint(1L, 0)).thenThrow(new CustomException(ErrorCode.INVALID_AMOUNT));

    // When & Then
    assertThatThrownBy(() -> useCase.execute(invalidCommand))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_AMOUNT);
  }
}