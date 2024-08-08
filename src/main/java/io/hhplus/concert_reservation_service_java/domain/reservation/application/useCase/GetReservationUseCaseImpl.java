package io.hhplus.concert_reservation_service_java.domain.reservation.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.GetReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.port.in.GetReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.ReservationMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetReservationUseCaseImpl implements GetReservationUseCase {
  private final ReservationService reservationService;
  private final ReservationMapper reservationMapper;


  @Override
  public ReservationDomain execute(GetReservationCommand command) {
    Reservation reservation = reservationService.getReservationToPay(command.getReservationId());
    return reservationMapper.from(reservation);
  }
}
