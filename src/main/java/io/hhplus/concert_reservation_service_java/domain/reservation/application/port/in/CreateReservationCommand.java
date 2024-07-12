package io.hhplus.concert_reservation_service_java.domain.reservation.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateReservationCommand {
  private long reserverId;
  private long concertScheduleId;
  private long seatId;
}
