package io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ConcertScheduleSeatJpaRepository extends JpaRepository<ConcertScheduleSeat, Long> {

  @Query("SELECT css.seat.id FROM ConcertScheduleSeat css WHERE css.concertSchedule.id = :concertScheduleId")
  List<Long> findSeatIdsByConcertScheduleId(long concertScheduleId);

  @Query("SELECT css.seat FROM ConcertScheduleSeat css WHERE css.concertSchedule.id = :concertScheduleId")
  List<Seat> findSeatsByConcertScheduleId(long concertScheduleId);

  @Query("SELECT css FROM ConcertScheduleSeat css WHERE css.concertSchedule.id = :concertScheduleId AND css.seat.id = :seatId")
  Optional<ConcertScheduleSeat> findConcertSceduleSeatByconcertScheduleIdAndseatId(long concertScheduleId, long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT css FROM ConcertScheduleSeat css WHERE css.concertSchedule.id = :concertScheduleId AND css.seat.id = :seatId")
    Optional<ConcertScheduleSeat> findConcertSceduleSeatByconcertScheduleIdAndseatIdWithLock(long concertScheduleId, long seatId);
}
