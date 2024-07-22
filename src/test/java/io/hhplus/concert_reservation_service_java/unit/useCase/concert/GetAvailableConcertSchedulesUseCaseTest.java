package io.hhplus.concert_reservation_service_java.unit.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetAvailableConcertSchedulesUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

class GetAvailableConcertSchedulesUseCaseTest {

  private final ConcertService concertService = Mockito.mock(ConcertService.class);
  private final ConcertScheduleMapper concertScheduleMapper = Mockito.mock(ConcertScheduleMapper.class);
  private final GetAvailableConcertSchedulesUseCase useCase = new GetAvailableConcertSchedulesUseCaseImpl(concertService, concertScheduleMapper);

  @Test
  @DisplayName("예약 가능한 콘서트 일정 조회 성공")
  void getAvailableSchedules_ReturnsListOfConcertScheduleDTOs() {
    // Given
    Long concertId = 1L;
    LocalDateTime now = LocalDateTime.now();
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();

    List<ConcertSchedule> concertSchedules = new ArrayList<>();
    List<ConcertScheduleDomain> concertScheduleDomains = new ArrayList<>();
    Concert concert = new Concert(concertId, "국립국악원 정기공연");
    for (int j=1; j <= 5; ++j){
      ConcertSchedule concertSchedule = ConcertSchedule.builder()
              .id((long)3+j)
                  .startAt(now.plusDays(j))
                      .capacity(j)
                          .build();
      concertSchedules.add(concertSchedule);
      concertScheduleDomains.add(new ConcertScheduleDomain(concertSchedule.getId(), concertSchedule.getStartAt(), concertSchedule.getCapacity()));
    }
    when(concertService.getUpcomingConcertSchedules(concertId)).thenReturn(concertSchedules);
    when(concertScheduleMapper.from(concertSchedules)).thenReturn(concertScheduleDomains);

    // When
    List<ConcertScheduleDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().hasSize(5).isEqualTo(concertScheduleDomains);
    verify(concertService).getUpcomingConcertSchedules(eq(concertId));
    verify(concertScheduleMapper).from(concertSchedules);
  }

  @Test
  @DisplayName("예약 가능한 콘서트 일정이 없을 때 - 빈 리스트 반환")
  void getNoAvailableSchedules_ReturnsEmptyList() {
    // Given
    Long concertId = 1L;
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();

    when(concertService.getUpcomingConcertSchedules(concertId)).thenReturn(
        Collections.emptyList());
    when(concertScheduleMapper.from(Collections.emptyList())).thenReturn(Collections.emptyList());

    // When
    List<ConcertScheduleDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().isEmpty();
    verify(concertService).getUpcomingConcertSchedules(eq(concertId));
    verify(concertScheduleMapper).from(Collections.emptyList());
  }

  @Test
  @DisplayName("concertService에서 null을 반환할 때 - 빈 List 반환")
  void getConcertsWithNullFromRepository_ReturnsEmptyList() {
    // Given
    Long concertId = 1L;
    when(concertService.getUpcomingConcertSchedules(concertId))
        .thenReturn(null);
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId)
        .build();

    // When
    List<ConcertScheduleDomain> result = useCase.execute(command);


    // Then
    assertThat(result).isNotNull().isEmpty();
    verify(concertService).getUpcomingConcertSchedules(concertId);
    verify(concertScheduleMapper, times(1)).from((List<ConcertSchedule>) null);
  }

  @Test
  @DisplayName("ConcertService에서 예외 발생")
  void ConcertServiceThrowsException() {
    // Given
    Long concertId = 1L;
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();
    when(concertService.getUpcomingConcertSchedules(1L)).thenThrow(new CustomException(ErrorCode.SERVICE));

    // When
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERVICE);
        });

    verify(concertService).getUpcomingConcertSchedules(1L);
    verify(concertScheduleMapper, never()).from((List<ConcertSchedule>)null);
  }


  @Test
  @DisplayName("ConcertMapper에서 예외 발생")
  void ConcertMapperThrowsException() {
    // Given
    Long concertId = 1L;
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();

    List<ConcertSchedule> mockSchedules = Arrays.asList(new ConcertSchedule(), new ConcertSchedule());
    when(concertService.getUpcomingConcertSchedules(1L)).thenReturn(mockSchedules);
    when(concertScheduleMapper.from(mockSchedules)).thenThrow(new CustomException(ErrorCode.MAPPER));

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MAPPER);
        });

    // Verify
    verify(concertService).getUpcomingConcertSchedules(1L);
    verify(concertScheduleMapper).from(mockSchedules);
  }
}