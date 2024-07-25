package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import java.util.List;

public interface ConcertService {

  List<ConcertSchedule> getUpcomingConcertSchedules(long concertId);

  List<Seat> getSeatsByConcertScheduleId(long concertScheduleId);

  List<Concert> getAll();

  List<ConcertSchedule> getAllConcertSchedulesByConcertId(long concertId);

  ConcertScheduleSeat getConcertScheduleSeat(long concertScheduleId, long seatId);

  ConcertScheduleSeat getConcertScheduleSeatWithLock(long concertScheduleId, long seatId);
}
