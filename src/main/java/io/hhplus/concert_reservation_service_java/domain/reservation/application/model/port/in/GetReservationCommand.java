package io.hhplus.concert_reservation_service_java.domain.reservation.application.model.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetReservationCommand {
  long reservationId;
}
