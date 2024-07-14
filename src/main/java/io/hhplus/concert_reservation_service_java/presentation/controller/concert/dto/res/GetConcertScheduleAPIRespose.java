package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;

public record GetConcertScheduleAPIRespose
    (List<ConcertScheduleDomain> concertSchedules)

{

  public static GetConcertScheduleAPIRespose from(List<ConcertScheduleDomain> result) {
    return new GetConcertScheduleAPIRespose(result);
  }
}
