package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import java.time.LocalDateTime;
import java.util.List;
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

}
