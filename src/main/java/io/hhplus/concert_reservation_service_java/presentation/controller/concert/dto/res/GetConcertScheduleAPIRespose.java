package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;

public record GetConcertScheduleAPIRespose
    (List<ConcertScheduleDTO> concertSchedules)

{

  public static GetConcertScheduleAPIRespose from(List<ConcertScheduleDTO> result) {
    return new GetConcertScheduleAPIRespose(result);
  }
}
