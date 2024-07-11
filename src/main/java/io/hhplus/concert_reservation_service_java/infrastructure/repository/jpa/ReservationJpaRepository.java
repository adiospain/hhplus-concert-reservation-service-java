package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

  @Query("SELECT r.seatId FROM Reservation r WHERE r.concertScheduleId = :concertScheduleId")
  List<Long> findAllSeatIdByConcertScheduleId(@Param("concertScheduleId") long concertScheduleId);
}
