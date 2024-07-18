package io.hhplus.concert_reservation_service_java.service;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.business.service.ConcertServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;


import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConcertServiceTest {
  private final ConcertRepository concertRepository = Mockito.mock(ConcertRepository.class);
  private final ConcertService service = new ConcertServiceImpl(concertRepository);

  @Test
  @DisplayName("현재 예약 가능한 콘서트 날짜 조회")
  void getUpcomingConcertSchedules() {
    long concertId = 1L;
    List<ConcertSchedule> expectedSchedules = Arrays.asList(new ConcertSchedule(), new ConcertSchedule());
    when(concertRepository.findUpcomingConcertSchedules(eq(concertId), any(LocalDateTime.class))).thenReturn(expectedSchedules);

    List<ConcertSchedule> result = service.getUpcomingConcertSchedules(concertId);

    assertEquals(expectedSchedules, result);
    verify(concertRepository).findUpcomingConcertSchedules(eq(concertId), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("현재 예약 가능한 콘서트 좌석 조회")
  void getSeatsByConcertScheduleId() {
    long concertScheduleId = 1L;
    List<Seat> expectedSeats = Arrays.asList(new Seat(), new Seat());
    when(concertRepository.findSeatsByConcertScheduleId(concertScheduleId)).thenReturn(expectedSeats);

    List<Seat> result = service.getSeatsByConcertScheduleId(concertScheduleId);

    assertEquals(expectedSeats, result);
    verify(concertRepository).findSeatsByConcertScheduleId(concertScheduleId);
  }

  @Test
  @DisplayName("모든 콘서트 조회")
  void getAll_withConcerts() {
    List<Concert> expectedConcerts = Arrays.asList(new Concert(), new Concert());
    when(concertRepository.findAll()).thenReturn(expectedConcerts);

    List<Concert> result = service.getAll();

    assertEquals(expectedConcerts, result);
    verify(concertRepository, times(2)).findAll();
  }

  @Test
  @DisplayName("모든 콘서트 조회 - 콘서트 없음")
  void getAll_withoutConcerts() {
    when(concertRepository.findAll()).thenReturn(Collections.emptyList());

    List<Concert> result = service.getAll();

    assertTrue(result.isEmpty());
    verify(concertRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("특정 콘서트의 날짜 조회")
  void getAllConcertSchedulesByConcertId() {
    long concertId = 1L;
    List<ConcertSchedule> expectedSchedules = Arrays.asList(new ConcertSchedule(), new ConcertSchedule());
    when(concertRepository.findAllConcertSchedulesByConcertId(concertId)).thenReturn(expectedSchedules);

    List<ConcertSchedule> result = service.getAllConcertSchedulesByConcertId(concertId);

    assertEquals(expectedSchedules, result);
    verify(concertRepository).findAllConcertSchedulesByConcertId(concertId);
  }

  @Test
  void getConcertScheduleSeat_found() {
    long concertScheduleId = 1L;
    long seatId = 2L;
    ConcertScheduleSeat expectedSeat = new ConcertScheduleSeat();
    when(concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId))
        .thenReturn(Optional.of(expectedSeat));

    ConcertScheduleSeat result = service.getConcertScheduleSeat(concertScheduleId, seatId);

    assertEquals(expectedSeat, result);
    verify(concertRepository).findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
  }

  @Test
  @DisplayName("예약 가능한 좌석 없음")
  void getConcertScheduleSeat_notFound() {
    long concertScheduleId = 1L;
    long seatId = 2L;
    when(concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId))
        .thenReturn(Optional.empty());

    assertThrows(
        CustomException.class, () -> service.getConcertScheduleSeat(concertScheduleId, seatId));
    verify(concertRepository).findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
  }
}