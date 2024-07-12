package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleSeatMapper;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetAvailableSeatsUseCaseImpl implements GetAvailableSeatsUseCae {

  private final ConcertRepository concertRepository;
  private final ReservationRepository reservationRepository;
  private final ConcertScheduleSeatMapper concertScheduleSeatMapper;

  @Override
  public List<ConcertScheduleSeatDTO> execute(GetAvailableSeatsCommand command) {
    try{
      CompletableFuture<List<Seat>> seatsFuture = CompletableFuture.supplyAsync(() ->
          concertRepository.findSeatsByConcertScheduleId(command.getConcertScheduleId()));
      CompletableFuture<Set<Long>> reservedSeatsFuture = CompletableFuture.supplyAsync(() ->
          new HashSet<>(reservationRepository.findSeatIdByconcertScheduleId(command.getConcertScheduleId())));

      List<Seat> allSeats = seatsFuture.join();
      Set<Long> reservedSeatIds = reservedSeatsFuture.join();
      return concertScheduleSeatMapper.AvailableSeatsFrom(allSeats, reservedSeatIds);
    } catch (CompletionException e){
      throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
    }

  }
}
