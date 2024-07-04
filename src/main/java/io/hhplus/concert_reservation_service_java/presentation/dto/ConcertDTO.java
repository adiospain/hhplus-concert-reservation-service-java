package io.hhplus.concert_reservation_service_java.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ConcertDTO {
  private long id;
  private String name;
  private List<ConcertScheduleDTO> concertSchedule;
}
