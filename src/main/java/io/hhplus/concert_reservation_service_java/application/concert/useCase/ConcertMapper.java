package io.hhplus.concert_reservation_service_java.application.concert.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

  @Component
  public class ConcertMapper {
    public ConcertDTO WithoutConcertScheduleFrom (Concert concert){
      return ConcertDTO.builder()
          .id(concert.getId())
          .name(concert.getName())
          .build();
    }

    public List<ConcertDTO> WithoutConcertScheduleFrom (List<Concert> concerts){
      if (concerts == null || concerts.isEmpty()){
        throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
      }
      return concerts.stream()
          .map(this::WithoutConcertScheduleFrom)
          .collect(Collectors.toList());
    }

    public ConcertDTO WithConcertScheduleFrom(List<ConcertSchedule> concertSchedules){
      if (concertSchedules == null || concertSchedules.isEmpty()){
        throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
      }
      Concert concert = concertSchedules.get(0).getConcert();
      return ConcertDTO.builder()
          .id(concert.getId())
          .name(concert.getName())
          .schedules(from(concertSchedules))
          .build();
    }

    private List<ConcertScheduleDTO> from(List<ConcertSchedule> concertSchedules) {
      return concertSchedules.stream()
          .map(this::from)
          .collect(Collectors.toList());
    }

    private ConcertScheduleDTO from(ConcertSchedule concertSchedule) {
      return ConcertScheduleDTO.builder()
          .id(concertSchedule.getId())
          .startAt(concertSchedule.getStartAt())
          .capacity(concertSchedule.getCapacity())
          .build();
    }
  }
