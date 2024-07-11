package io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.useCase;

import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

class GetAvailableConcertSchedulesUseCaseTest {

  private final ConcertRepository concertRepository = Mockito.mock(ConcertRepository.class);
  private final ConcertScheduleMapper concertScheduleMapper = Mockito.mock(ConcertScheduleMapper.class);
  private final GetAvailableConcertSchedulesUseCase useCase = new GetAvailableConcertSchedulesUseCaseImpl(concertRepository, concertScheduleMapper);

  @Test
  @DisplayName("예약 가능한 콘서트 일정 조회 성공")
  void getAvailableSchedules_ReturnsListOfConcertScheduleDTOs() {
    // Given
    Long concertId = 1L;
    LocalDateTime now = LocalDateTime.now();
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();

    List<ConcertSchedule> concertSchedules = new ArrayList<>();
    List<ConcertScheduleDTO> concertScheduleDTOs = new ArrayList<>();
    Concert concert = new Concert(concertId, "국립국악원 정기공연");
    for (int j=1; j <= 5; ++j){
      ConcertSchedule concertSchedule = new ConcertSchedule();
      concertSchedule.setId((long)3+j);
      concertSchedule.setStartAt(now.plusDays(j));
      concertSchedule.setCapacity(j);
      concertSchedules.add(concertSchedule);
      concertScheduleDTOs.add(new ConcertScheduleDTO(concertSchedule.getId(), concertSchedule.getStartAt(), concertSchedule.getCapacity()));
    }



    when(concertRepository.findUpcomingConcertSchedules(any(long.class), any(LocalDateTime.class))).thenReturn(concertSchedules);
    when(concertScheduleMapper.from(concertSchedules)).thenReturn(concertScheduleDTOs);

    // When
    List<ConcertScheduleDTO> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().hasSize(5).isEqualTo(concertScheduleDTOs);

    verify(concertRepository).findUpcomingConcertSchedules(eq(concertId), any(LocalDateTime.class));
    verify(concertScheduleMapper).from(concertSchedules);
  }

  @Test
  @DisplayName("예약 가능한 콘서트 일정이 없을 때 - 빈 리스트 반환")
  void getNoAvailableSchedules_ReturnsEmptyList() {
    // Given
    Long concertId = 1L;
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();

    when(concertRepository.findUpcomingConcertSchedules(eq(concertId), any(LocalDateTime.class))).thenReturn(
        Collections.emptyList());
    when(concertScheduleMapper.from(Collections.emptyList())).thenReturn(Collections.emptyList());

    // When
    List<ConcertScheduleDTO> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().isEmpty();

    verify(concertRepository).findUpcomingConcertSchedules(eq(concertId), any(LocalDateTime.class));
    verify(concertScheduleMapper).from(Collections.emptyList());
  }
}