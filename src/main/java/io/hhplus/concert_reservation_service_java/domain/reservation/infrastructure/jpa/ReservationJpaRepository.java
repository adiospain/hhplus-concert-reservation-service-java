package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

  @Query("SELECT r.seatId FROM Reservation r WHERE r.concertScheduleId = :concertScheduleId AND (r.status = 'OCCUPIED' OR r.status = 'PAID')")
  List<Long> findOccupiedSeatIdByconcertScheduleId(long concertScheduleId);

  @Modifying
  @Query("UPDATE Reservation r SET r.status = 'EXPIRED' WHERE r.createdAt < :now")
  int bulkUpdateExpiredReservations(@Param("now") LocalDateTime now);

  @Modifying
  @Query("DELETE FROM Reservation r WHERE r.status = 'EXPIRED' AND r.createdAt < :expirationTime")
  void deleteExpiredReservations(@Param("expirationTime") LocalDateTime expirationTime);
}
