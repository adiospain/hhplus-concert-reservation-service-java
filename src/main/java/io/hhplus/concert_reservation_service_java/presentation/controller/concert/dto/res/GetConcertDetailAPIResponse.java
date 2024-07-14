package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record GetConcertDetailAPIResponse

    (ConcertDomain concert){


  public static GetConcertDetailAPIResponse from(ConcertDomain result) {
    return new GetConcertDetailAPIResponse(result);
  }
}
