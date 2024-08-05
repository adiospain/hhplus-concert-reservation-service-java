package io.hhplus.concert_reservation_service_java.domain.reservation;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;

public interface CreateReservationUseCase {

  ReservationDomain execute(CreateReservationCommand command);
}
