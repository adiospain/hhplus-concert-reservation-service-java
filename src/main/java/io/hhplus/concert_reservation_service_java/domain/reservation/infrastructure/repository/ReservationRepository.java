package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

  Reservation save(Reservation reservation);

  Optional<Reservation> findById(long reservationId);

  List<Long> findOccupiedSeatIdByconcertScheduleId(long concertScheduleId);

  int bulkUpdateExpiredReservations(LocalDateTime now);

  void deleteExpiredReservations(LocalDateTime localDateTime);

  List<Reservation> findAll();
}
