package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.List;

public record GetSeatAPIRespose (
    List<ConcertScheduleSeatDTO> seats
)

{
  public static GetSeatAPIRespose from(List<ConcertScheduleSeatDTO> result) {
    return new GetSeatAPIRespose(result);
  }
}
