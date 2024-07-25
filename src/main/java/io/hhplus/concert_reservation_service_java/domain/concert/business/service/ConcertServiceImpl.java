package io.hhplus.concert_reservation_service_java.domain.concert.business.service;

import io.hhplus.concert_reservation_service_java.core.common.common.redisson.DistributedLock;
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
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConcertServiceImpl implements ConcertService {
  private final ConcertRepository concertRepository;


  private final RedissonClient redissonClient;

  @Override
  public List<ConcertSchedule> getUpcomingConcertSchedules(long concertId) {
    if (concertId <= 0){
      throw new CustomException(ErrorCode.INVALID_CONCERT);
    }
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
    List<Concert> concerts = concertRepository.findAll();
    if (concerts.isEmpty()){
      return Collections.emptyList();
    }
    return concertRepository.findAll();
  }

  @Override
  public List<ConcertSchedule> getAllConcertSchedulesByConcertId(long concertId) {
    return concertRepository.findAllConcertSchedulesByConcertId(concertId);
  }

  @Override
  public ConcertScheduleSeat getConcertScheduleSeat(long concertScheduleId, long seatId) {
    String lockKey = "reservation:" + concertScheduleId + ":" + seatId;
    RLock lock = redissonClient.getLock(lockKey);


    try{
      boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
      if (!isLocked) {
        throw new CustomException(ErrorCode.ALREADY_RESERVED);
      }
      return concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(concertScheduleId, seatId)
          .orElseThrow(()->new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ConcertScheduleSeat getConcertScheduleSeatWithLock(long concertScheduleId, long seatId) {
    return concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatIdWithLock(concertScheduleId, seatId)
        .orElseThrow(()->new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND));
  }
}
