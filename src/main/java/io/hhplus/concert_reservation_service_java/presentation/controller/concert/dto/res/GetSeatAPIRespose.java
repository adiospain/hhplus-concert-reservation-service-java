package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.List;

public record GetSeatAPIRespose (
    List<ConcertScheduleSeatDomain> seats
)

{
  public static GetSeatAPIRespose from(List<ConcertScheduleSeatDomain> result) {
    return new GetSeatAPIRespose(result);
  }
}
