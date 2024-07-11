package io.hhplus.concert_reservation_service_java.application.concert.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetConcertDetailCommand {
  private long concertId;
}
