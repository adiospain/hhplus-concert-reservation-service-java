package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

  List<Long> findSeatIdByconcertScheduleId(long concertScheduleId);

  Reservation save(Reservation reservation);

  Optional<Reservation> findById(long reservationId);
}
