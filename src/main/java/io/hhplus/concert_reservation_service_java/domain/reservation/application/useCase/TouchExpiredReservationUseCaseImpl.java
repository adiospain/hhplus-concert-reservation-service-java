package io.hhplus.concert_reservation_service_java.domain.reservation.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.TouchExpiredReservationUseCase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class TouchExpiredReservationUseCaseImpl implements TouchExpiredReservationUseCase {
  private final ReservationService reservationService;

  @Override
  public void execute() {
    int updatedCount = reservationService.bulkUpdateExpiredReservations();
    if (updatedCount > 0){
      reservationService.deleteExpiredReservations();
    }
  }
}
