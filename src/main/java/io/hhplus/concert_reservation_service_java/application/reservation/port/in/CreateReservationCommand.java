package io.hhplus.concert_reservation_service_java.application.reservation.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateReservationCommand {
  private long reserverId;
  private long concertScheduleId;
  private long seatId;
}
