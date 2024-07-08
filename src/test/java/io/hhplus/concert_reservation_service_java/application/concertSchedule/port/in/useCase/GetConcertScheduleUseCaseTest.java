package io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.useCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetConcertScheduleCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertScheduleRepository;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.GetConcertScheduleUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.concertSchedule.dto.ConcertScheduleDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetConcertScheduleUseCaseTest {

  private final ConcertScheduleRepository concertScheduleRepository = Mockito.mock(ConcertScheduleRepository.class);
  private final GetConcertScheduleUseCase getConcertScheduleUseCase = new GetConcertScheduleUseCaseImpl(concertScheduleRepository);
  @Test
  @DisplayName("콘서트의 모든 날짜 목록 조회 성공")
  void getConcerts_Success(){
    LocalDateTime fixedDateTime = LocalDateTime.of(2025, 6, 1, 10, 0, 0);

    List<ConcertSchedule> concertSchedules = new ArrayList<>();
    Concert concert = new Concert(34L, "국립국악원 정기공연");
    for (int j=0; j <5; ++j){
      ConcertSchedule concertSchedule = new ConcertSchedule();
      concertSchedule.setId((long)3+j);
      concertSchedule.setStartAt(fixedDateTime.plusDays(j));
      concertSchedule.setCapacity(j);
      concertSchedules.add(concertSchedule);
    }
    when(concertScheduleRepository.findByConcertId(34L)).thenReturn(concertSchedules);
    GetConcertScheduleCommand command = GetConcertScheduleCommand.builder()
        .concertId(34L)
        .available(false)
        .build();

    List<ConcertScheduleDTO> result = getConcertScheduleUseCase.execute(command);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    assertThat(result.get(0).getId()).isEqualTo(3L);
    assertThat(result.get(0).getStartAt()).isEqualTo(fixedDateTime);
    assertThat(result.get(0).getCapacity()).isEqualTo(0);
    assertThat(result.get(1).getId()).isEqualTo(4L);
    assertThat(result.get(1).getStartAt()).isEqualTo(fixedDateTime.plusDays(1));
    assertThat(result.get(1).getCapacity()).isEqualTo(1);

    verify(concertScheduleRepository, times(1)).findByConcertId(34L);
  }

  @Test
  @DisplayName("콘서트 예약 가능 날짜 목록 조회 성공")
  void getConcertsAvailable_Success(){
    LocalDateTime fixedDateTime = LocalDateTime.of(2025, 6, 1, 10, 0, 0);

    List<ConcertSchedule> concertSchedules = new ArrayList<>();
    Concert concert = new Concert(34L, "국립국악원 정기공연");
    for (int j=0; j <5; ++j){
      ConcertSchedule concertSchedule = new ConcertSchedule();
      concertSchedule.setId((long)3+j);
      concertSchedule.setStartAt(fixedDateTime.plusDays(j));
      concertSchedule.setCapacity(j);
      concertSchedules.add(concertSchedule);
    }
    when(concertScheduleRepository.findByConcertId(34L)).thenReturn(concertSchedules);
    GetConcertScheduleCommand command = GetConcertScheduleCommand.builder()
        .concertId(34L)
        .available(true)
        .build();

    List<ConcertScheduleDTO> result = getConcertScheduleUseCase.execute(command);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(4);
    assertThat(result.get(0).getId()).isEqualTo(4L);
    assertThat(result.get(0).getStartAt()).isEqualTo(fixedDateTime.plusDays(1));
    assertThat(result.get(0).getCapacity()).isEqualTo(1);
    assertThat(result.get(1).getId()).isEqualTo(5L);
    assertThat(result.get(1).getStartAt()).isEqualTo(fixedDateTime.plusDays(2));
    assertThat(result.get(1).getCapacity()).isEqualTo(2);

    verify(concertScheduleRepository, times(1)).findByConcertId(34L);
  }
}