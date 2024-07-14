package io.hhplus.concert_reservation_service_java.domain.concert.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ConcertScheduleSeatDomain {
  private Long id;
  private int seatNumber;
}