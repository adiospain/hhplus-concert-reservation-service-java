package io.hhplus.concert_reservation_service_java.domain.reservation.application.service;

import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;

  @Override
  public Set<Long> getSeatsIdByconcertScheduleId(long concertScheduleId) {
    return new HashSet<>(reservationRepository.findOccupiedSeatIdByconcertScheduleId(concertScheduleId));
  }
}
