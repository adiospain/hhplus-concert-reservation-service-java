package io.hhplus.concert_reservation_service_java.unit.service;

import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.business.service.ReservationServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
  private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
  private final ReservationService service = new ReservationServiceImpl(reservationRepository);

  @Test
  @DisplayName("예약 불가 좌석 조회")
  void testGetSeatsIdByconcertScheduleId() {
    long concertScheduleId = 1L;
    List<Long> occupiedSeats = Arrays.asList(1L, 2L, 3L);
    when(reservationRepository.findOccupiedSeatIdByconcertScheduleId(concertScheduleId)).thenReturn(occupiedSeats);

    Set<Long> result = service.getSeatsIdByconcertScheduleId(concertScheduleId);

    assertEquals(new HashSet<>(occupiedSeats), result);
    verify(reservationRepository).findOccupiedSeatIdByconcertScheduleId(concertScheduleId);
  }

  @Test
  @DisplayName("예약 조회")
  void testGetById_ExistingReservation() {
    long reservationId = 1L;
    Reservation expectedReservation = new Reservation();
    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(expectedReservation));

    Reservation result = service.getById(reservationId);

    assertEquals(expectedReservation, result);
    verify(reservationRepository).findById(reservationId);
  }
  @Test
  @DisplayName("존재하지 않은 예약 id 조회 - 예외처리")
  void testGetById_NonExistingReservation() {
    long reservationId = 1L;
    when(reservationRepository.findById(reservationId)).thenThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    assertThrows(CustomException.class, () -> service.getById(reservationId));
    verify(reservationRepository).findById(reservationId);
  }

  @Test
  @DisplayName("유효한 예약 찾기")
  void testGetReservationToPay_ValidReservation() {
    long reservationId = 1L;
    long userId = 2L;
    int point = 200;
    User user = new User(userId, point);
    Reservation reservation = Reservation.builder()
        .id(reservationId)
        .user(user)
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now().minusMinutes(4))
        .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    Reservation result = service.getReservationToPay(reservationId);

    assertEquals(reservation, result);
    verify(reservationRepository).findById(reservationId);
  }
  @Test
  @DisplayName("결제를 하려다가 이미 결제된 예약 조회 - 예외처리")
  void testGetReservationToPay_AlreadyPaid() {
    long reservationId = 1L;
    long userId = 2L;
    Reservation reservation = Reservation.builder()
        .id(reservationId)
        .status(ReservationStatus.PAID)
        .createdAt(LocalDateTime.now().minusMinutes(2))
        .build();


    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    assertThatThrownBy(() -> service.getReservationToPay(reservationId))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", (ErrorCode.INVALID_RESERVATION_STATUS));
    verify(reservationRepository).findById(reservationId);
  }

  @Test
  @DisplayName("결제를 위한 유효한 예약 조회")
  void testGetReservationToPay_Expired() {
    long reservationId = 1L;
    long userId = 2L;
    int point = 200;

    User user = new User(userId, point);
    Reservation reservation = Reservation.builder()
        .user(user)
        .id(reservationId)
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now().minusMinutes(6))
        .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    assertThatThrownBy(() -> service.getReservationToPay(reservationId))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", (ErrorCode.INVALID_RESERVATION_STATUS));
    verify(reservationRepository).findById(reservationId);
  }

  @Test
  @DisplayName("예약과 사용자 불일치 - 실패")
  void testGetReservationToPay_UserMismatch() {
    // Arrange
    long reservationId = 1L;
    long userId = 10L;
    long differentUserId = 20L;

    User user = new User(differentUserId, 3000);
    Reservation reservation = Reservation.builder()
        .id(reservationId)
        .user(user)
        .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    assertThatThrownBy(() -> service.getReservationToPay(reservationId))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", (ErrorCode.RESERVATION_AND_USER_NOT_MATCHED));

  }

  @Test
  @DisplayName("결제를 하려다가 시간상 만료되었지만 상태전환 되지 않은 예약 조회")
  void testGetReservationToPay_ExpiredButNotStaus() {
    long reservationId = 1L;
    long userId = 2L;
    Reservation reservation = Reservation.builder()
        .id(reservationId)
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now().minusMinutes(10))
        .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
    assertThatThrownBy(() -> service.getReservationToPay(reservationId))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", (ErrorCode.EXPIRED_RESERVATION));
    verify(reservationRepository).findById(reservationId);
  }

  @Test
  @DisplayName("결제를 하려다가 시간상 만료되었지만 상태전환 되지 않은 예약 조회")
  void testSaveToCreate() {
    Reservation reservation = new Reservation();
    when(reservationRepository.save(reservation)).thenReturn(reservation);

    Reservation result = service.saveToCreate(reservation);

    assertEquals(reservation, result);
    verify(reservationRepository).save(reservation);
  }

  @Test
  @DisplayName("결제 완료 후 예약상태 변경 후 저장")
  void testSaveToPay_SuccessfulSave() {
    Reservation reservation = new Reservation();
    when(reservationRepository.save(reservation)).thenReturn(reservation);

    Reservation result = service.saveToPay(reservation);

    assertEquals(reservation, result);
    assertEquals(ReservationStatus.PAID, reservation.getStatus());
    verify(reservationRepository).save(reservation);
  }
  @Test
  @DisplayName("결제를 하려다가 시간상 만료되었지만 상태전환 되지 않은 예약 조회")
  void testSaveToPay_UniqueConstraintViolation_AlreadyReserved() {
    Reservation reservation = Reservation.builder()
        .concertScheduleId(1L)
        .seatId(1L)
        .build();

    when(reservationRepository.save(reservation)).thenThrow(
        new CustomException(ErrorCode.ALREADY_RESERVED));

    assertThrows(CustomException.class, () -> service.saveToPay(reservation));
    verify(reservationRepository).save(reservation);
  }

  @Test
  void testSaveToPay_UniqueConstraintViolation_ReservationFail() {
    Reservation reservation = Reservation.builder()
        .concertScheduleId(1L)
        .seatId(1L)
        .build();


    when(reservationRepository.save(reservation)).thenThrow(
        new CustomException(ErrorCode.RESERVATION_FAILED));

    assertThrows(CustomException.class, () -> service.saveToPay(reservation));
    verify(reservationRepository).save(reservation);
  }
}