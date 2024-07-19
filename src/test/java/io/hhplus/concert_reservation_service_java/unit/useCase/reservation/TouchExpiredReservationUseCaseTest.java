package io.hhplus.concert_reservation_service_java.unit.useCase.reservation;

import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.TouchExpiredReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.useCase.TouchExpiredReservationUseCaseImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class TouchExpiredReservationUseCaseTest {

  private final ReservationService reservationService = Mockito.mock(ReservationService.class);
  private final TouchExpiredReservationUseCase useCase = new TouchExpiredReservationUseCaseImpl(reservationService);

  @Test
  @DisplayName("한 개 이상의 만료된 예약이 있을 경우")
  void execute_WhenUpdatedCountIsGreaterThanZero_ShouldDeleteExpiredReservations() {
    // Given
    when(reservationService.bulkUpdateExpiredReservations()).thenReturn(5);

    // When
    useCase.execute();

    // Then
    verify(reservationService).bulkUpdateExpiredReservations();
    verify(reservationService).deleteExpiredReservations();
  }

  @Test
  @DisplayName("만료된 예약이 없을 경우")
  void execute_WhenUpdatedCountIsZero_ShouldNotDeleteExpiredReservations() {
    // Given
    when(reservationService.bulkUpdateExpiredReservations()).thenReturn(0);

    // When
    useCase.execute();

    // Then
    verify(reservationService).bulkUpdateExpiredReservations();
    verify(reservationService, never()).deleteExpiredReservations();
  }

  @Test
  @DisplayName("벌크 업데이트 실패할 경우 - 예약 삭제 진행 안함")
  void execute_WhenBulkUpdateThrowsException_ShouldNotDeleteExpiredReservations() {
    // Given
    when(reservationService.bulkUpdateExpiredReservations()).thenThrow(new RuntimeException("Bulk update failed"));

    // When
    try {
      useCase.execute();
    } catch (RuntimeException e) {
      // Expected exception
    }

    // Then
    verify(reservationService).bulkUpdateExpiredReservations();
    verify(reservationService, never()).deleteExpiredReservations();
  }
}
