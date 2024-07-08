package io.hhplus.concert_reservation_service_java.presentation.controller.concertSchedule.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.dto.SeatDTO;
import java.util.List;

public record GetSeatAPIRespose (
    List<SeatDTO> seats
)

{
  public static GetSeatAPIRespose from(List<SeatDTO> result) {
    return new GetSeatAPIRespose(result);
  }
}
