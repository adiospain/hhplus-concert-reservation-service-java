package io.hhplus.concert_reservation_service_java.domain.reservation.application.service;

import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
  @Transactional
  public Reservation getById(long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
  }

  @Override
  public Reservation save(Reservation reservation){
    return reservationRepository.save(reservation);
  }
}
