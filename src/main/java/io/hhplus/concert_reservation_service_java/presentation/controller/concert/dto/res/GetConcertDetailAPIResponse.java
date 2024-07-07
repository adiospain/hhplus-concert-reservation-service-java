package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record GetConcertDetailAPIResponse

    (ConcertDTO concert){


  public static GetConcertDetailAPIResponse from(ConcertDTO result) {
    return new GetConcertDetailAPIResponse(result);
  }
}
