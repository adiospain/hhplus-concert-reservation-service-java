package io.hhplus.concert_reservation_service_java.application.concertScheduleSeat.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetAvailableSeatsCommand {
  private long concertId;
  private long concertScheduleId;
}
