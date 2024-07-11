package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;


public interface ConcertRepository {

  List<Concert > findAll();

  Optional<Concert> findById(long concertId);

  List<ConcertSchedule> findAllConcertSchedulesByConcertId(long concertId);

  List<Long> findSeatIdsByConcertScheduleId(long concertScheduleId);

  List<Seat> findSeatsByConcertScheduleId(long concertScheduleId);

  Optional<ConcertSchedule> findConcertScheduleByConcertSceduleId(long concertScheduleId);

  Optional<ConcertScheduleSeat> findConcertSceduleSeatByconcertScheduleIdAndseatId(long concertScheduleId, long SeatId);

  ConcertSchedule save(ConcertSchedule concertSchedule);

  List<ConcertSchedule> findUpcomingConcertSchedules(long concertId, LocalDateTime now);
}
