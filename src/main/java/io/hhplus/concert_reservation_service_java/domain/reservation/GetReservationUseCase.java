package io.hhplus.concert_reservation_service_java.domain.reservation;

import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.port.in.GetReservationCommand;

public interface GetReservationUseCase {

  ReservationDomain execute(GetReservationCommand command);
}
