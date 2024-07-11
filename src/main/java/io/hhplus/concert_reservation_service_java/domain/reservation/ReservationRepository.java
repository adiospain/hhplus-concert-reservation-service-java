package io.hhplus.concert_reservation_service_java.domain.reservation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReservationRepository {

  List<Long> findSeatIdByconcertScheduleId(long concertScheduleId);

  Reservation save(Reservation reservation);
}
