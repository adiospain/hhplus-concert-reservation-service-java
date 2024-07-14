package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record GetConcertsAPIResponse(Page<ConcertDomain> concerts) {

  public static GetConcertsAPIResponse from(List<ConcertDomain> result, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), result.size());

    if (start > result.size()) {
      return new GetConcertsAPIResponse(new PageImpl<>(List.of(), pageable, result.size()));
    }

    List<ConcertDomain> subList = result.subList(start, end);
    Page<ConcertDomain> concertPage = new PageImpl<>(subList, pageable, result.size());
    return new GetConcertsAPIResponse(concertPage);
  }
}
