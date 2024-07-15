package io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.res;

import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;

public record CreateReservationAPIResponse
    (ReservationDomain reservation)
{

  public static CreateReservationAPIResponse from(ReservationDomain reservation) {
    return new CreateReservationAPIResponse(reservation);
  }
}
