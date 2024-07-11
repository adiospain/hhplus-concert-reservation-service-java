package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ConcertDTO {
  private long id;
  private String name;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<ConcertScheduleDTO> schedules;
}
