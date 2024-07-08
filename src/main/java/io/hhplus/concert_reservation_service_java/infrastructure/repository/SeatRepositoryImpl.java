package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.domain.seat.SeatRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.SeatJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SeatRepositoryImpl implements SeatRepository {
  private final SeatJpaRepository seatRepository;


  @Override
  public List<Seat> findByConcertScheduleId(Long concertScheduleId) {
    return seatRepository.findByConcertScheduleId(concertScheduleId);
  }
}
