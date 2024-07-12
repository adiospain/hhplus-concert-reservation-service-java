package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto;

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

}
