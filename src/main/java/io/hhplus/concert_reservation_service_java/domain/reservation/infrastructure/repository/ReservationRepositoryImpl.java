package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
  private final ReservationJpaRepository reservationRepository;

  @Override
  public Reservation save(Reservation reservation) {
    return reservationRepository.save(reservation);
  }

  @Override
  public Optional<Reservation> findById(long reservationId) {
    return reservationRepository.findById(reservationId);
  }

  @Override
  public List<Long> findOccupiedSeatIdByconcertScheduleId(long concertScheduleId) {
    return reservationRepository.findOccupiedSeatIdByconcertScheduleId(concertScheduleId);
  }

  @Override
  public int bulkUpdateExpiredReservations(LocalDateTime now) {
    return reservationRepository.bulkUpdateExpiredReservations(now);
  }

  @Override
  @Transactional
  public void deleteExpiredReservations(LocalDateTime expirationTime) {
    reservationRepository.deleteExpiredReservations(expirationTime);
  }

  @Override
  public List<Reservation> findAll() {
    return reservationRepository.findAll();
  }


}
