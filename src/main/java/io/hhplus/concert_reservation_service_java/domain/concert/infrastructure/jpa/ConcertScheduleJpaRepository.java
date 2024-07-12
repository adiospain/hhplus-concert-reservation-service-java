package io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {

  @Query("SELECT cs FROM ConcertSchedule cs WHERE cs.concert.id = :concertId")
  List<ConcertSchedule> findAllByConcertId(@Param("concertId") Long concertId);

  @Query("SELECT cs FROM ConcertSchedule cs WHERE cs.concert.id = :concertId AND cs.startAt > :currentTime ORDER BY cs.startAt")
  List<ConcertSchedule> findUpcomingConcertSchedules(
      @Param("concertId") Long concertId,
      @Param("currentTime") LocalDateTime currentTime
  );
}
