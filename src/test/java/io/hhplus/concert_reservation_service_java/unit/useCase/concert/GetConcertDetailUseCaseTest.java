package io.hhplus.concert_reservation_service_java.unit.useCase.concert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetConcertDetailUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

class GetConcertDetailUseCaseTest {


  private final ConcertService concertService = Mockito.mock(ConcertService.class);
  private final ConcertMapper concertMapper = Mockito.mock(ConcertMapper.class);
  private final GetConcertDetailUseCase useCase = new GetConcertDetailUseCaseImpl(concertService, concertMapper);

  private List<Concert> concerts = new ArrayList<>();
  private List<ConcertSchedule> concertSchedules = new ArrayList<>();
  LocalDateTime fixedDateTime;
  @BeforeEach
  void setUp(){
    fixedDateTime = LocalDateTime.of(2025, 6, 1, 10, 0, 0);
    for (int i=1; i <= 35; ++i){
      LocalDateTime localDateTime = LocalDateTime.now();
      concertSchedules = new ArrayList<>();
      Concert concert = new Concert((long)i, "아이유 콘서트"+i);
      for (int j=1; j <= 50; ++j){
        ConcertSchedule concertSchedule = new ConcertSchedule(concert, fixedDateTime.plusDays(j), 42);
        concertSchedule.setId((long)j);
        concertSchedules.add(concertSchedule);
      }
      concerts.add(concert);
    }

  }

  @Test
  @DisplayName("콘서트 상세 조회 성공")
  void getConcerts_Success(){
    long concertId = 3L;
    LocalDateTime fixedDateTime = LocalDateTime.now().plusYears(1);

    List<ConcertSchedule> concertSchedules = new ArrayList<>();
    List<ConcertScheduleDomain> concertScheduleDomains = new ArrayList<>();
    Concert concert = new Concert(concertId, "국립국악원 정기공연");
    for (int j=0; j <5; ++j){
      ConcertSchedule concertSchedule = new ConcertSchedule();
      concertSchedule.setId((long)3+j);
      concertSchedule.setStartAt(fixedDateTime.plusDays(j));
      concertSchedule.setCapacity(j);
      concertSchedules.add(concertSchedule);
      concertScheduleDomains.add(new ConcertScheduleDomain(concertSchedule.getId(), concertSchedule.getStartAt(), concertSchedule.getCapacity()));
    }


    ConcertDomain concertDomain = new ConcertDomain(concert.getId(), concert.getName(), concertScheduleDomains);

    when(concertService.getAllConcertSchedulesByConcertId(any(Long.class))).thenReturn(concertSchedules);
    when(concertMapper.WithConcertScheduleFrom(concertSchedules)).thenReturn(concertDomain);
    GetConcertDetailCommand command = GetConcertDetailCommand.builder().concertId(concerts.get(0).getId()).build();
    ConcertDomain result = useCase.execute(command);

    assertThat(result.getId()).isEqualTo(concert.getId());
    assertThat(result.getName()).isEqualTo(concert.getName());
    assertThat(result.getSchedules().get(0).getId()).isEqualTo(concertScheduleDomains.get(0).getId());
    assertThat(result.getSchedules().get(0).getStartAt()).isEqualTo(concertScheduleDomains.get(0).getStartAt());
    assertThat(result.getSchedules().get(0).getCapacity()).isEqualTo(concertScheduleDomains.get(0).getCapacity());
    verify(concertService).getAllConcertSchedulesByConcertId(concerts.get(0).getId());
    verify(concertMapper).WithConcertScheduleFrom(concertSchedules);
  }

  @Test
  @DisplayName("존재하지 않는 콘서트 ID로 조회 시 예외 발생")
  void execute_WithNonExistentConcertId_ThrowsException() {
    // Given
    when(concertService.getAllConcertSchedulesByConcertId(1L)).thenReturn(Collections.emptyList());
    when(concertMapper.WithConcertScheduleFrom(Collections.emptyList()))
        .thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

    // When & Then
    GetConcertDetailCommand command = GetConcertDetailCommand.builder().concertId(concerts.get(0).getId()).build();
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);

    // Then
    verify(concertService).getAllConcertSchedulesByConcertId(1L);
    verify(concertMapper).WithConcertScheduleFrom(Collections.emptyList());
  }
  @Test
  @DisplayName("ConcertService에서 null 반환 시 예외 발생")
  void execute_WithNullFromService_ThrowsException() {
    // Given
    when(concertService.getAllConcertSchedulesByConcertId(1L)).thenReturn(null);
    when(concertMapper.WithConcertScheduleFrom(null))
        .thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

    // When
    GetConcertDetailCommand command = GetConcertDetailCommand.builder().concertId(concerts.get(0).getId()).build();
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);

    // Then
    verify(concertService).getAllConcertSchedulesByConcertId(1L);
    verify(concertMapper).WithConcertScheduleFrom(null);
  }

}