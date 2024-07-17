package io.hhplus.concert_reservation_service_java.domain.reservation;

import java.util.Set;

public interface ReservationService {

  Set<Long> getSeatsIdByconcertScheduleId(long concertScheduleId);
}
