package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto;

import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ConcertScheduleSeatDTO {
  private Long id;
  private int seatNumber;
}
