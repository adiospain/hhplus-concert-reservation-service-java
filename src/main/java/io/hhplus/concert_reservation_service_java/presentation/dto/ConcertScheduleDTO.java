package io.hhplus.concert_reservation_service_java.presentation.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ConcertScheduleDTO {
  private Long concertScheduleId;
  private LocalDateTime openAt;
  private int seats;
}
