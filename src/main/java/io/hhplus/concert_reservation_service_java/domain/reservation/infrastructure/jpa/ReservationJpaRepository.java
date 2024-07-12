package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

  @Query("SELECT r.seatId FROM Reservation r WHERE r.concertScheduleId = :concertScheduleId")
  List<Long> findAllSeatIdByConcertScheduleId(@Param("concertScheduleId") long concertScheduleId);
}
