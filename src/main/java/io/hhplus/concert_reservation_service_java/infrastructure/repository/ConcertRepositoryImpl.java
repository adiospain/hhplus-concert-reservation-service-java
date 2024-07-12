package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.ConcertJpaRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.ConcertScheduleJpaRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.ConcertScheduleSeatJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

  private final ConcertJpaRepository concertRepository;
  private final ConcertScheduleJpaRepository concertScheduleRepository;
  private final ConcertScheduleSeatJpaRepository concertScheduleSeatJpaRepository; 

  @Override
  public List<Concert> findAll() {
    return concertRepository.findAll();
  }

  @Override
  public Optional<Concert> findById(long concertId) {
    return concertRepository.findById(concertId);
  }

  @Override
  public List<ConcertSchedule> findAllConcertSchedulesByConcertId(long concertId) {
    return concertScheduleRepository.findAllByConcertId(concertId);
  }

  @Override
  public List<Long> findSeatIdsByConcertScheduleId(long concertScheduleId) {
    return concertScheduleSeatJpaRepository.findSeatIdsByConcertScheduleId(concertScheduleId);
  }

  @Override
  public List<Seat> findSeatsByConcertScheduleId(long concertScheduleId) {
    return concertScheduleSeatJpaRepository.findSeatsByConcertScheduleId(concertScheduleId);
  }

  @Override
  public Optional<ConcertSchedule> findConcertScheduleByConcertSceduleId(long concertScheduleId) {
    return concertScheduleRepository.findById(concertScheduleId);
  }

  @Override
  public Optional<ConcertScheduleSeat> findConcertSceduleSeatByconcertScheduleIdAndseatId(
      long concertScheduleId, long seatId) {
    return concertScheduleSeatJpaRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId);
  }

  @Override
  public ConcertSchedule save(ConcertSchedule concertSchedule) {
    return concertScheduleRepository.save(concertSchedule);
  }

  @Override
  public List<ConcertSchedule> findUpcomingConcertSchedules(long concertId, LocalDateTime now) {
    return concertScheduleRepository.findUpcomingConcertSchedules(concertId, now);
  }
}
