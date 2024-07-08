package io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetConcertScheduleCommand {
  private long concertId;
  private boolean available;
}
