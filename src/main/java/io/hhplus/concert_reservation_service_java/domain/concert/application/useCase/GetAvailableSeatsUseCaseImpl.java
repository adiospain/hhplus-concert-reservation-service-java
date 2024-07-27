package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleSeatMapper;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetAvailableSeatsUseCaseImpl implements GetAvailableSeatsUseCae {
  private final ConcertService concertService;
  private final ReservationService reservationService;
  private final ConcertScheduleSeatMapper concertScheduleSeatMapper;

  @Override
  public List<ConcertScheduleSeatDomain> execute(GetAvailableSeatsCommand command) {
    List<Seat> allSeats = concertService.getSeatsByConcertScheduleId(
        command.getConcertScheduleId());
    Set<Long> reservedSeatIds = reservationService.getSeatsIdByconcertScheduleId(
        command.getConcertScheduleId());
    return concertScheduleSeatMapper.AvailableSeatsFrom(allSeats, reservedSeatIds);
  }
}
