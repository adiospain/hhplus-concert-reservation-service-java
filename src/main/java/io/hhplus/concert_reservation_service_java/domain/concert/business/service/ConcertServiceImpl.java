package io.hhplus.concert_reservation_service_java.domain.concert.business.service;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConcertServiceImpl implements ConcertService {
  private final ConcertRepository concertRepository;


  @Override
  public List<ConcertSchedule> getUpcomingConcertSchedules(long concertId) {
    List<ConcertSchedule> concertSchedules = concertRepository.findUpcomingConcertSchedules(concertId, LocalDateTime.now());
    return concertSchedules;
  }

  @Override
  public List<Seat> getSeatsByConcertScheduleId(long concertScheduleId) {
    List<Seat> seats = concertRepository.findSeatsByConcertScheduleId(concertScheduleId);
    return seats;
  }

  @Override
  public List<Concert> getAll() {
    return concertRepository.findAll();
  }

  @Override
  public List<ConcertSchedule> getAllConcertSchedulesByConcertId(long concertId) {
    return concertRepository.findAllConcertSchedulesByConcertId(concertId);
  }
}
