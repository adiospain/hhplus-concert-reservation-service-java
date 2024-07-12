package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
  private final ReservationJpaRepository reservationRepository;

  @Override
  public List<Long> findSeatIdByconcertScheduleId(long concertScheduleId) {
    return reservationRepository.findAllSeatIdByConcertScheduleId(concertScheduleId);
  }


  @Override
  public Reservation save(Reservation reservation) {
    return reservationRepository.save(reservation);
  }

  @Override
  public Optional<Reservation> findById(long reservationId) {
    return Optional.empty();
  }


}
