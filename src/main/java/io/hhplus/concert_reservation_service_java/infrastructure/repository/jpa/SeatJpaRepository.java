package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.domain.seat.SeatId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatJpaRepository extends JpaRepository<Seat, SeatId> {

  List<Seat> findByConcertScheduleId(Long concertScheduleId);
}
