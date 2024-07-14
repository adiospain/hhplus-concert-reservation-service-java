package io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.useCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetAvailableConcertSchedulesUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;

import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetConcertScheduleUseCaseTest {

  private final ConcertRepository concertRepository = Mockito.mock(ConcertRepository.class);
  private final ConcertScheduleMapper concertScheduleMapper = Mockito.mock(ConcertScheduleMapper.class);
  private final GetAvailableConcertSchedulesUseCase getAvailableConcertSchedulesUseCase = new GetAvailableConcertSchedulesUseCaseImpl(concertRepository, concertScheduleMapper);

  @Test
  @DisplayName("예약 가능한 콘서트 날짜가 있을 때")
  void getConcerts_ReturnsList(){
    long concertId = 3L;
    LocalDateTime fixedDateTime = LocalDateTime.now().plusYears(1);

    List<ConcertSchedule> concertSchedules = new ArrayList<>();
    Concert concert = new Concert(concertId, "국립국악원 정기공연");
    for (int j=0; j <5; ++j){
      ConcertSchedule concertSchedule = new ConcertSchedule();
      concertSchedule.setId((long)3+j);
      concertSchedule.setStartAt(fixedDateTime.plusDays(j));
      concertSchedule.setCapacity(j);
      concertSchedules.add(concertSchedule);
    }
    LocalDateTime now = LocalDateTime.now();
    List<ConcertScheduleDTO> concertScheduleDTOs = concertSchedules.stream()
        .map(cs -> new ConcertScheduleDTO(cs.getId(), cs.getStartAt(), cs.getCapacity()))
        .collect(Collectors.toList());
    when(concertRepository.findUpcomingConcertSchedules(any(Long.class), any(LocalDateTime.class)))
        .thenReturn(concertSchedules);
    when(concertScheduleMapper.from(any(List.class))).thenReturn(concertScheduleDTOs);

    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId)
        .build();

    List<ConcertScheduleDomain> result = getAvailableConcertSchedulesUseCase.execute(command);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    assertThat(result.get(0).getId()).isEqualTo(3L);
    assertThat(result.get(0).getStartAt()).isEqualTo(fixedDateTime);
    assertThat(result.get(0).getCapacity()).isEqualTo(0);
    assertThat(result.get(1).getId()).isEqualTo(4L);
    assertThat(result.get(1).getStartAt()).isEqualTo(fixedDateTime.plusDays(1));
    assertThat(result.get(1).getCapacity()).isEqualTo(1);

    verify(concertRepository, times(1)).findUpcomingConcertSchedules(any(long.class), any(LocalDateTime.class));
    verify(concertScheduleMapper, times(1)).from(any(List.class));
  }

  @Test
  @DisplayName("예약 가능한 콘서트 날짜가 없을 때")
  void getConcertsButNoAvailable_ReturnsEmptyList(){
    Long concertId = 2L;
    when(concertRepository.findUpcomingConcertSchedules(any(long.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
    when(concertScheduleMapper.from(any(List.class))).thenReturn(Collections.emptyList());
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId)
        .build();

    List<ConcertScheduleDomain> result = getAvailableConcertSchedulesUseCase.execute(command);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(concertRepository, times(1)).findUpcomingConcertSchedules(any(long.class), any(LocalDateTime.class));
    verify(concertScheduleMapper, times(1)).from(Collections.emptyList());
  }
  @Test
  @DisplayName("Repository에서 null을 반환할 때 - 빈 List 반환")
  void getConcertsWithNullFromRepository_ReturnsEmptyList() {

    Long concertId = 1L;
    when(concertRepository.findUpcomingConcertSchedules(any(Long.class), any(LocalDateTime.class)))
        .thenReturn(null);

    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId)
        .build();
    List<ConcertScheduleDomain> result = getAvailableConcertSchedulesUseCase.execute(command);


    assertThat(result).isNotNull().isEmpty();
    verify(concertRepository, times(1)).findUpcomingConcertSchedules(any(long.class), any(LocalDateTime.class));
    verify(concertScheduleMapper, times(1)).from((List<ConcertSchedule>) null);
  }
}