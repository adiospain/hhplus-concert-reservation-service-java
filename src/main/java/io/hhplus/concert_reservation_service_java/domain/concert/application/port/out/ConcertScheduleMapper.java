package io.hhplus.concert_reservation_service_java.domain.concert.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConcertScheduleMapper {

  public ConcertScheduleDTO from(ConcertSchedule concertSchedule) {
    if (concertSchedule == null) {
      throw new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND);
    }

    return ConcertScheduleDTO.builder()
        .id(concertSchedule.getId())
        .startAt(concertSchedule.getStartAt())
        .capacity(concertSchedule.getCapacity())
        .build();
  }

  public List<ConcertScheduleDTO> from(List<ConcertSchedule> concertSchedules) {
    if (concertSchedules == null) {
      return Collections.emptyList();
    }
    return concertSchedules.stream()
        .map(this::from)
        .collect(Collectors.toList());
  }
}