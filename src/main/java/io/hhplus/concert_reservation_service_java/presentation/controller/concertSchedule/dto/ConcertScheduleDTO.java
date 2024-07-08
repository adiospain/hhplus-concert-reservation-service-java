package io.hhplus.concert_reservation_service_java.presentation.controller.concertSchedule.dto;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ConcertScheduleDTO {
  private long id;
  private LocalDateTime startAt;
  private int capacity;

  public static ConcertScheduleDTO from(ConcertSchedule concertSchedule) {
    return ConcertScheduleDTO.builder()
        .id(concertSchedule.getId())
        .startAt(concertSchedule.getStartAt())
        .capacity(concertSchedule.getCapacity())
        .build();
  }
}
