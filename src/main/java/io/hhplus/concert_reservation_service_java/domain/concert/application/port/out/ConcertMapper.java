package io.hhplus.concert_reservation_service_java.domain.concert.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConcertMapper {
  public ConcertDomain WithoutConcertScheduleFrom (Concert concert){
    return ConcertDomain.builder()
        .id(concert.getId())
        .name(concert.getName())
        .build();
  }

  public List<ConcertDomain> WithoutConcertScheduleFrom (List<Concert> concerts){
    if (concerts == null || concerts.isEmpty()){
      throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
    }
    return concerts.stream()
        .map(this::WithoutConcertScheduleFrom)
        .collect(Collectors.toList());
  }

  public ConcertDomain WithConcertScheduleFrom(List<ConcertSchedule> concertSchedules){
    if (concertSchedules == null || concertSchedules.isEmpty()){
      throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
    }
    Concert concert = concertSchedules.get(0).getConcert();
    return ConcertDomain.builder()
        .id(concert.getId())
        .name(concert.getName())
        .schedules(from(concertSchedules))
        .build();
  }

  private List<ConcertScheduleDomain> from(List<ConcertSchedule> concertSchedules) {
    return concertSchedules.stream()
        .map(this::from)
        .collect(Collectors.toList());
  }

  private ConcertScheduleDomain from(ConcertSchedule concertSchedule) {
    return ConcertScheduleDomain.builder()
        .id(concertSchedule.getId())
        .startAt(concertSchedule.getStartAt())
        .capacity(concertSchedule.getCapacity())
        .build();
  }
}
