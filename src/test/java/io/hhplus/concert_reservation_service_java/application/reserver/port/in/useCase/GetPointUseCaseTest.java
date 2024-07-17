package io.hhplus.concert_reservation_service_java.application.reserver.port.in.useCase;

import io.hhplus.concert_reservation_service_java.domain.reserver.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverService;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.GetPointCommand;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase.ChargePointUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase.GetPointUseCaseImpl;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

class GetPointUseCaseTest {
  private final ReserverService reserverService = Mockito.mock(ReserverService.class);
  private final GetPointUseCase useCase = new GetPointUseCaseImpl(reserverService);

  @Test
  @DisplayName("유저 포인트 조회 성공")
  void execute_ShouldReturnPointFromReserverService() {
    // Given
    long reserverId = 1L;
    int expectedPoint = 100;
    GetPointCommand command = GetPointCommand.builder()
            .reserverId(reserverId)
              .build();
    when(reserverService.getPoint(reserverId)).thenReturn(expectedPoint);

    // When
    int result = useCase.execute(command);

    // Then
    assertEquals(expectedPoint, result);
    verify(reserverService).getPoint(reserverId);
  }

  @Test
  @DisplayName("예약자를 찾을 수 없을 때 - CustomException")
  void execute_WhenReserverServiceThrowsException_ShouldThrowException() {
    // Given
    long reserverId = 1L;
    int expectedPoint = 100;
    GetPointCommand command = GetPointCommand.builder()
        .reserverId(reserverId)
        .build();
    when(reserverService.getPoint(reserverId)).thenThrow(new CustomException(ErrorCode.RESERVER_NOT_FOUND));

    // When
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESERVER_NOT_FOUND);
        });

    // Then
    verify(reserverService).getPoint(reserverId);
  }
}