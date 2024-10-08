package io.hhplus.concert_reservation_service_java.domain.concert.business.service;

import io.hhplus.concert_reservation_service_java.core.common.annotation.DistributedLock;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConcertServiceImpl implements ConcertService {
  private final ConcertRepository concertRepository;


  @Override
  public List<ConcertSchedule> getUpcomingConcertSchedules(long concertId) {
    if (concertId <= 0){
      throw new CustomException(ErrorCode.INVALID_CONCERT);
    }
    List<ConcertSchedule> concertSchedules = concertRepository.findUpcomingConcertSchedules(concertId, LocalDateTime.now());
      return concertSchedules;
  }


  @Override
  @Cacheable(value="Seats", key = "'concertSchedule:'+ #concertScheduleId")
  public List<Seat> getSeatsByConcertScheduleId(long concertScheduleId) {
    List<Seat> seats = concertRepository.findSeatsByConcertScheduleId(concertScheduleId);
    return seats;
  }

  @Override
  @Cacheable(value="Concert", key="'allConcerts'")
  @DistributedLock(key = "'concerts:'+ 'all'", leaseTime = 30, waitTime = 10)
  public List<Concert> getAll() {
    List<Concert> concerts = concertRepository.findAll();
    if (concerts.isEmpty()){
      return Collections.emptyList();
    }
    return concertRepository.findAll();
  }

  @Override
  @Cacheable(value = "concertSchedules", key = "'concert:' + #concertId + ':concertSchedules'")
  @DistributedLock(key = "'concertSchedules:'+ #concertId", leaseTime = 30, waitTime = 10)
  public List<ConcertSchedule> getAllConcertSchedulesByConcertId(long concertId) {
    return concertRepository.findAllConcertSchedulesByConcertId(concertId);
  }

  @Override
  @DistributedLock(key = "'concertSchedule:'+ #concertScheduleId + ':seat:' + #seatId", leaseTime = 5 * 60L, waitTime = 0L, unlockAfter = false)
  public ConcertScheduleSeat getConcertScheduleSeat(long concertScheduleId, long seatId) {
    ConcertScheduleSeat concertScheduleSeat = concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId)
        .orElseThrow(()->new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND));
      return concertScheduleSeat;
  }

  @Override
  public ConcertScheduleSeat getConcertScheduleSeatWithLock(long concertScheduleId, long seatId) {
    return concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatIdWithLock(concertScheduleId, seatId)
        .orElseThrow(()->new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND));
  }
}
