package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertScheduleRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.ConcertScheduleJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {

  private final ConcertScheduleJpaRepository concertScheduleRepository;

  @Override
  public List<ConcertSchedule> findByConcertId(Long concertId) {
    return concertScheduleRepository.findByConcertId(concertId);
  }
}
