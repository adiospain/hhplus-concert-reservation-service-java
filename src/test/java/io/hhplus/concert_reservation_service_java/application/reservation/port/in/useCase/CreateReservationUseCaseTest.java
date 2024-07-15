package io.hhplus.concert_reservation_service_java.application.reservation.port.in.useCase;

import io.hhplus.concert_reservation_service_java.domain.reservation.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.useCase.CreateReservationUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.port.out.ReservationMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.model.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateReservationUseCaseTest {

  private ReserverRepository reserverRepository;
  private ReservationRepository reservationRepository;
  private ConcertRepository concertRepository;
  private ReservationMapper reservationMapper;
  private TokenService tokenService;
  private CreateReservationUseCaseImpl createReservationUseCase;

  @BeforeEach
  void setUp() {
    reserverRepository = Mockito.mock(ReserverRepository.class);
    reservationRepository = Mockito.mock(ReservationRepository.class);
    concertRepository = Mockito.mock(ConcertRepository.class);
    reservationMapper = Mockito.mock(ReservationMapper.class);
    tokenService = Mockito.mock(TokenService.class);
    createReservationUseCase = new CreateReservationUseCaseImpl(
        reserverRepository, reservationRepository, concertRepository, reservationMapper, tokenService);
  }

  @Test
  void 예약_성공() {
    long reserverId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
        .reserverId(reserverId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId).build();

    Reserver reserver = new Reserver(reserverId, 34000);

    ConcertSchedule concertSchedule = new ConcertSchedule();
    concertSchedule.setId(concertScheduleId);

    Seat seat = new Seat();
    seat.setId(seatId);

    ConcertScheduleSeat concertScheduleSeat = new ConcertScheduleSeat();
    concertScheduleSeat.setId(1L);
    concertScheduleSeat.setConcertSchedule(concertSchedule);
    concertScheduleSeat.setSeat(seat);
    concertScheduleSeat.setPrice(25000);

    Reservation reservation = reserver.createReservation(concertScheduleSeat);
    reservation.setId(1L);
    ReservationDomain reservationDomain = new ReservationDomain(reservation.getId(), reservation.getCreatedAt(), reservation.getCreatedAt().plusMinutes(5));

    when(reserverRepository.findById(reserverId)).thenReturn(Optional.of(reserver));
    when(concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId))
        .thenReturn(Optional.of(concertScheduleSeat));
    when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
    when(reservationMapper.from(any(Reservation.class))).thenReturn(reservationDomain);

    ReservationDomain result = createReservationUseCase.execute(command);


    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(reservation.getId());
    assertThat(result.getCreatedAt()).isEqualTo(reservation.getCreatedAt());

    verify(reserverRepository).findById(reserverId);
    verify(concertRepository).findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
    verify(reservationRepository).save(any(Reservation.class));
    verify(reservationMapper).from(reservation);
  }

  @Test
  void 예약자_없음_예외() {
    // Given
    long reserverId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
        .reserverId(reserverId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId).build();

    when(reserverRepository.findById(reserverId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVER_NOT_FOUND);

    verify(reserverRepository).findById(reserverId);
    verify(concertRepository, never()).findConcertSceduleSeatByconcertScheduleIdAndseatId(anyLong(), anyLong());
    verify(reservationRepository, never()).save(any(Reservation.class));
    verify(reservationMapper, never()).from(any(Reservation.class));
  }

  @Test
  void 콘서트_일정_좌석_없음_예외() {
    // Given
    long reserverId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
        .reserverId(reserverId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId).build();

    Reserver reserver = new Reserver(reserverId, 62000);

    when(reserverRepository.findById(reserverId)).thenReturn(Optional.of(reserver));
    when(concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId))
        .thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND);

    verify(reserverRepository).findById(reserverId);
    verify(concertRepository).findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
    verify(reservationRepository, never()).save(any(Reservation.class));
    verify(reservationMapper, never()).from(any(Reservation.class));
  }

  @Test
  void 예약_중복_예외() {
    // Given
    long reserverId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
        .reserverId(reserverId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId).build();

    Reserver reserver = new Reserver(reserverId, 32000);
    ConcertSchedule concertSchedule = new ConcertSchedule();
    concertSchedule.setId(concertScheduleId);

    Seat seat = new Seat();
    seat.setId(seatId);

    ConcertScheduleSeat concertScheduleSeat = new ConcertScheduleSeat();
    concertScheduleSeat.setId(1L);
    concertScheduleSeat.setConcertSchedule(concertSchedule);
    concertScheduleSeat.setSeat(seat);
    concertScheduleSeat.setPrice(25000);

    Reservation reservation = reserver.createReservation(concertScheduleSeat);

    when(reserverRepository.findById(reserverId)).thenReturn(Optional.of(reserver));
    when(concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId))
        .thenReturn(Optional.of(concertScheduleSeat));
    when(reservationRepository.save(any(Reservation.class)))
        .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_RESERVED);

    verify(reserverRepository).findById(reserverId);
    verify(concertRepository).findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
    verify(reservationRepository).save(any(Reservation.class));
    verify(reservationMapper, never()).from(any(Reservation.class));
  }

  @Test
  void 예약_실패_예외() {
    // Given
    long reserverId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
    .reserverId(reserverId)
    .concertScheduleId(concertScheduleId)
        .seatId(seatId).build();

    Reserver reserver = new Reserver(reserverId, 10000);
    ConcertSchedule concertSchedule = new ConcertSchedule();
    concertSchedule.setId(concertScheduleId);

    Seat seat = new Seat();
    seat.setId(seatId);

    ConcertScheduleSeat concertScheduleSeat = new ConcertScheduleSeat();
    concertScheduleSeat.setId(1L);
    concertScheduleSeat.setConcertSchedule(concertSchedule);
    concertScheduleSeat.setSeat(seat);
    concertScheduleSeat.setPrice(25000);

    when(reserverRepository.findById(reserverId)).thenReturn(Optional.of(reserver));
    when(concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId))
        .thenReturn(Optional.of(concertScheduleSeat));
    when(reservationRepository.save(any(Reservation.class)))
        .thenThrow(new RuntimeException("Unexpected error"));

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_FAILED);

    verify(reserverRepository).findById(reserverId);
    verify(concertRepository).findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
    verify(reservationRepository).save(any(Reservation.class));
    verify(reservationMapper, never()).from(any(Reservation.class));
  }
}