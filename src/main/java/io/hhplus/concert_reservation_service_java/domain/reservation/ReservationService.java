package io.hhplus.concert_reservation_service_java.domain.reservation;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReservationService {

  Set<Long> getSeatsIdByconcertScheduleId(long concertScheduleId);

  Reservation getById(long reservationId);

  Reservation saveToPay(Reservation reservation);
  Reservation getReservationToPay(long reservationId);

  Reservation saveToCreate(Reservation savedReservation);

  void expire(Reservation reservation);


  void deleteExpiredReservations();

  int bulkUpdateExpiredReservations();
}
