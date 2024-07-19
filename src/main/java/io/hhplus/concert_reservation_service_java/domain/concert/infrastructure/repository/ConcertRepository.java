package io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ConcertRepository {

  List<Concert> findAll();

  Optional<Concert> findById(long concertId);

  List<ConcertSchedule> findAllConcertSchedulesByConcertId(long concertId);

  List<Long> findSeatIdsByConcertScheduleId(long concertScheduleId);

  List<Seat> findSeatsByConcertScheduleId(long concertScheduleId);

  Optional<ConcertSchedule> findConcertScheduleByConcertSceduleId(long concertScheduleId);

  Optional<ConcertScheduleSeat> findConcertSceduleSeatByconcertScheduleIdAndseatId(long concertScheduleId, long SeatId);

  Concert save(Concert concert);

  ConcertSchedule save(ConcertSchedule concertSchedule);

  List<ConcertSchedule> findUpcomingConcertSchedules(long concertId, LocalDateTime now);

  void deleteAll();
}
