package io.hhplus.concert_reservation_service_java.application.concert.port.in.useCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetConcertDetailUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
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


  private final ConcertRepository concertRepository = Mockito.mock(ConcertRepository.class);
  private final ConcertMapper concertMapper = Mockito.mock(ConcertMapper.class);
  private final GetConcertDetailUseCase getConcertDetailUseCase = new GetConcertDetailUseCaseImpl(concertRepository, concertMapper);

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
    List<ConcertScheduleDTO> concertScheduleDTOs = new ArrayList<>();
    Concert concert = new Concert(concertId, "국립국악원 정기공연");
    for (int j=0; j <5; ++j){
      ConcertSchedule concertSchedule = new ConcertSchedule();
      concertSchedule.setId((long)3+j);
      concertSchedule.setStartAt(fixedDateTime.plusDays(j));
      concertSchedule.setCapacity(j);
      concertSchedules.add(concertSchedule);
      concertScheduleDTOs.add(new ConcertScheduleDTO(concertSchedule.getId(), concertSchedule.getStartAt(), concertSchedule.getCapacity()));
    }


    ConcertDTO concertDTO = new ConcertDTO(concert.getId(), concert.getName(), concertScheduleDTOs);

    when(concertRepository.findAllConcertSchedulesByConcertId(any(Long.class))).thenReturn(concertSchedules);
    when(concertMapper.WithConcertScheduleFrom(concertSchedules)).thenReturn(concertDTO);
    GetConcertDetailCommand command = GetConcertDetailCommand.builder().concertId(concerts.get(0).getId()).build();
    ConcertDTO result = getConcertDetailUseCase.execute(command);

    assertThat(result.getId()).isEqualTo(concert.getId());
    assertThat(result.getName()).isEqualTo(concert.getName());
    assertThat(result.getSchedules().get(0).getId()).isEqualTo(concertScheduleDTOs.get(0).getId());
    assertThat(result.getSchedules().get(0).getStartAt()).isEqualTo(concertScheduleDTOs.get(0).getStartAt());
    assertThat(result.getSchedules().get(0).getCapacity()).isEqualTo(concertScheduleDTOs.get(0).getCapacity());
    verify(concertRepository).findAllConcertSchedulesByConcertId(concerts.get(0).getId());
    verify(concertMapper).WithConcertScheduleFrom(concertSchedules);
  }

  @Test
  @DisplayName("존재하지 않는 콘서트 ID로 조회 시 예외 발생")
  void execute_WithNonExistentConcertId_ThrowsException() {
    // Given
    Long nonExistentConcertId = 999L;
    GetConcertDetailCommand command = GetConcertDetailCommand.builder().concertId(nonExistentConcertId).build();

    when(concertRepository.findAllConcertSchedulesByConcertId(nonExistentConcertId))
        .thenReturn(Collections.emptyList());
    when(concertMapper.WithConcertScheduleFrom(Collections.emptyList()))
        .thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

    // When & Then
    assertThatThrownBy(() -> getConcertDetailUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);

    verify(concertRepository).findAllConcertSchedulesByConcertId(nonExistentConcertId);
    verify(concertMapper).WithConcertScheduleFrom(Collections.emptyList());
  }
  @Test
  @DisplayName("Repository에서 null 반환 시 예외 발생")
  void execute_WithNullFromRepository_ThrowsException() {
    // Given
    Long concertId = 1L;
    GetConcertDetailCommand command = GetConcertDetailCommand.builder().concertId(concertId).build();

    when(concertRepository.findAllConcertSchedulesByConcertId(concertId)).thenReturn(null);
    when(concertMapper.WithConcertScheduleFrom(null))
        .thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

    assertThatThrownBy(() -> getConcertDetailUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);

    verify(concertRepository).findAllConcertSchedulesByConcertId(concertId);
    verify(concertMapper).WithConcertScheduleFrom(null);
  }

}