package io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.ReservationDTO;

public record CreateReservationAPIResponse
    (ReservationDTO reservation)
{

  public static CreateReservationAPIResponse from(ReservationDTO reservation) {
    return new CreateReservationAPIResponse(reservation);
  }
}
