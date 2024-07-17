package io.hhplus.concert_reservation_service_java.domain.reservation.business.service;

import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;

  @Override
  public Set<Long> getSeatsIdByconcertScheduleId(long concertScheduleId) {
    return new HashSet<>(reservationRepository.findOccupiedSeatIdByconcertScheduleId(concertScheduleId));
  }

  @Override
  @Transactional (readOnly = true)
  public Reservation getById(long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
  }

  @Override
  public Reservation getReservationToPay(long reservationId) {
    Reservation reservation = this.getById(reservationId);
    if (reservation.getStatus() != ReservationStatus.OCCUPIED) {
      throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
    }
    if (reservation.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
      throw new CustomException(ErrorCode.EXPIRED_RESERVATION);
    }
    return reservation;
  }

  @Override
  public Reservation saveToCreate(Reservation reservation) {
    return reservationRepository.save(reservation);
  }

  @Override
  public Reservation saveToPay(Reservation reservation){
    reservation.completeReservation();
    try {
      return reservationRepository.save(reservation);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage().contains("unique index") ||
          e.getMessage().contains("unique constraint") ||
          e.getMessage().contains("primary key violation") ||
          e.getMessage().contains("UK_concert_schedule_seat")) {
        throw new CustomException(ErrorCode.ALREADY_RESERVED,
            "콘서트 날짜 : " + reservation.getConcertScheduleId() + "좌석 : " + reservation.getSeatId() +
                "콘서트 날짜 ID: " + reservation.getConcertScheduleId() +
                ", 좌석 ID: " + reservation.getSeatId());
      } else {
        throw new CustomException(ErrorCode.RESERVATION_FAILED,
            "콘서트 날짜 : " + reservation.getConcertScheduleId() + "좌석 : " + reservation.getSeatId() +
                "콘서트 날짜 ID: " + reservation.getConcertScheduleId() +
                ", 좌석 ID: " + reservation.getSeatId());
      }
    } catch (Exception e) {
      throw new CustomException(ErrorCode.RESERVATION_FAILED,
          "예약 중 예기치 않은 오류가 발생했습니다." +
              "콘서트 날짜 : " + reservation.getConcertScheduleId() + "좌석 : " + reservation.getSeatId() +
              "콘서트 날짜 ID: " + reservation.getConcertScheduleId() +
              ", 좌석 ID: " + reservation.getSeatId());
    }
  }
}