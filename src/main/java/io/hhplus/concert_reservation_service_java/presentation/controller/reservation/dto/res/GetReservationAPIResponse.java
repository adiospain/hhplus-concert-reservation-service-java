package io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.res;

import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;

public record GetReservationAPIResponse (ReservationDomain reservation) {

  public static GetReservationAPIResponse from(ReservationDomain reservation) {
    return new GetReservationAPIResponse (reservation);
  }
}
