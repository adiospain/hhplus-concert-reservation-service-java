package io.hhplus.concert_reservation_service_java.application.seat.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetSeatCommand {
  private long concertScheduleId;
  private boolean available;
}
