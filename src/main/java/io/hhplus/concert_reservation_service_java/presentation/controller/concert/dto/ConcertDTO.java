package io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto;

import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import java.util.List;
import java.util.stream.Collectors;
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
  private List<ConcertScheduleDTO> schedules;

  public static ConcertDTO from(Concert concert) {
    List<ConcertScheduleDTO> schedules = concert.getSchedules().stream()
        .map(ConcertScheduleDTO::from)
        .collect(Collectors.toList());
    return ConcertDTO.builder()
        .id(concert.getId())
        .name(concert.getName())
        .schedules(schedules)
        .build();
  }
}
