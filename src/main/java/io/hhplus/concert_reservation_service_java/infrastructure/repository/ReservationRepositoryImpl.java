package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.ReservationJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
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


}
