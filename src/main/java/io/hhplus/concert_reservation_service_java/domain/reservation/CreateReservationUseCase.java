package io.hhplus.concert_reservation_service_java.domain.reservation;

import io.hhplus.concert_reservation_service_java.domain.reservation.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.ReservationDTO;

public interface CreateReservationUseCase {

  ReservationDTO execute(CreateReservationCommand command);
}
