package io.hhplus.concert_reservation_service_java.domain.concertSchedule;

import java.util.List;

public interface ConcertScheduleRepository {

  List<ConcertSchedule> findAllByConcertId(long concertId);
}
