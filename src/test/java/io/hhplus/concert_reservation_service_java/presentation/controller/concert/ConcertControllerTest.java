package io.hhplus.concert_reservation_service_java.presentation.controller.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertDetailAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertScheduleAPIRespose;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertsAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetSeatAPIRespose;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class ConcertControllerTest {

  private final GetConcertsUseCase getConcertsUseCase= Mockito.mock(GetConcertsUseCase.class);
  private final GetConcertDetailUseCase getConcertDetailUseCase= Mockito.mock(GetConcertDetailUseCase.class);
  private final GetAvailableConcertSchedulesUseCase getAvailableConcertSchedulesUseCase = Mockito.mock(GetAvailableConcertSchedulesUseCase.class);
  private final GetAvailableSeatsUseCae getAvailableSeatsUseCae = Mockito.mock(GetAvailableSeatsUseCae.class);;

  private final ConcertController concertController = new ConcertController(
      getConcertsUseCase,
      getConcertDetailUseCase,
      getAvailableConcertSchedulesUseCase,
      getAvailableSeatsUseCae
  );;


  @Test
  @DisplayName("콘서트 조회 - Paging 10")
  void getConcerts_ShouldReturnListOf10Concerts() {
    List<ConcertDomain> concerts = new ArrayList<>();
    for (int i = 1; i <= 15; i++) {
      concerts.add(new ConcertDomain((long) i, "Concert " + i, null));
    }
    when(getConcertsUseCase.execute()).thenReturn(concerts);

    ResponseEntity<GetConcertsAPIResponse> response = concertController.getConcerts(0, 10);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    Page<ConcertDomain> resultPage = response.getBody().concerts();
    assertThat(resultPage.getContent()).hasSize(10);
    assertThat(resultPage.getTotalElements()).isEqualTo(15);
    assertThat(resultPage.getTotalPages()).isEqualTo(2);
    assertThat(resultPage.getNumber()).isEqualTo(0);

    verify(getConcertsUseCase).execute();
  }

  @Test
  @DisplayName("콘서트 상세정보 조회")
  void getConcertDetail_ShouldReturnConcertDetail() {
    // Given
    Long concertId = 1L;
    List<ConcertScheduleDomain> schedules = Arrays.asList(
        new ConcertScheduleDomain(1L, LocalDateTime.now().plusDays(1), 0),
        new ConcertScheduleDomain(2L, LocalDateTime.now().plusDays(2), 0)
    );
    ConcertDomain concertDomain = new ConcertDomain(concertId, "Concert 1", schedules);

    when(getConcertDetailUseCase.execute(any(GetConcertDetailCommand.class))).thenReturn(concertDomain);

    // When
    ResponseEntity<GetConcertDetailAPIResponse> response = concertController.getConcertDetail(concertId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    GetConcertDetailAPIResponse apiResponse = response.getBody();
    assertThat(apiResponse.concert()).isNotNull();
    assertThat(apiResponse.concert().getId()).isEqualTo(concertId);
    assertThat(apiResponse.concert().getName()).isEqualTo("Concert 1");
    assertThat(apiResponse.concert().getSchedules()).hasSize(2);
    assertThat(apiResponse.concert().getSchedules().get(0).getId()).isEqualTo(1L);
    assertThat(apiResponse.concert().getSchedules().get(1).getId()).isEqualTo(2L);

    verify(getConcertDetailUseCase).execute(any(GetConcertDetailCommand.class));
  }

  @Test
  @DisplayName("에약 가능한 콘서트 날짜 조회")
  void getAvailableConcertSchedules_ShouldReturnAvailableSchedules() {
    // Given
    Long concertId = 1L;
    LocalDateTime now = LocalDateTime.now();
    List<ConcertScheduleDomain> schedules = Arrays.asList(
        new ConcertScheduleDomain(1L, now, 100),
        new ConcertScheduleDomain(2L, now.plusDays(1), 150)
    );
    when(getAvailableConcertSchedulesUseCase.execute(any(GetAvailableConcertSchedulesCommand.class)))
        .thenReturn(schedules);

    // When
    ResponseEntity<GetConcertScheduleAPIRespose> response = concertController.getAvailableConcertSchedules(concertId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    GetConcertScheduleAPIRespose apiResponse = response.getBody();
    assertThat(apiResponse.concertSchedules()).isNotNull();
    assertThat(apiResponse.concertSchedules()).hasSize(2);

    ConcertScheduleDomain firstSchedule = apiResponse.concertSchedules().get(0);
    assertThat(firstSchedule.getId()).isEqualTo(1L);
    assertThat(firstSchedule.getStartAt()).isEqualTo(now);
    assertThat(firstSchedule.getCapacity()).isEqualTo(100);

    ConcertScheduleDomain secondSchedule = apiResponse.concertSchedules().get(1);
    assertThat(secondSchedule.getId()).isEqualTo(2L);
    assertThat(secondSchedule.getStartAt()).isEqualTo(now.plusDays(1));
    assertThat(secondSchedule.getCapacity()).isEqualTo(150);

    verify(getAvailableConcertSchedulesUseCase, times(1)).execute(any(GetAvailableConcertSchedulesCommand.class));
  }

  @Test
  @DisplayName("에약 가능한 콘서트 좌석 조회")
  void getAvailableSeats_ShouldReturnAvailableSeats() {
    Long concertId = 1L;
    Long concertScheduleId = 1L;
    List<ConcertScheduleSeatDomain> seats = Arrays.asList(
        new ConcertScheduleSeatDomain(1L, 1),
        new ConcertScheduleSeatDomain(2L, 2)
    );
    when(getAvailableSeatsUseCae.execute(any(GetAvailableSeatsCommand.class))).thenReturn(seats);

    ResponseEntity<GetSeatAPIRespose> response = concertController.getAvailableSeats(concertId, concertScheduleId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    GetSeatAPIRespose apiResponse = response.getBody();
    assertThat(apiResponse.seats()).hasSize(2);
    verify(getAvailableSeatsUseCae).execute(any(GetAvailableSeatsCommand.class));
  }
}