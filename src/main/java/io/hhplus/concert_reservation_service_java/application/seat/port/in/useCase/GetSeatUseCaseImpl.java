package io.hhplus.concert_reservation_service_java.application.seat.port.in.useCase;

import io.hhplus.concert_reservation_service_java.application.seat.port.in.GetSeatCommand;
import io.hhplus.concert_reservation_service_java.core.configuration.swagger.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.seat.GetSeatUseCae;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.domain.seat.SeatRepository;
import io.hhplus.concert_reservation_service_java.presentation.dto.SeatDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetSeatUseCaseImpl implements GetSeatUseCae {

  private final SeatRepository seatRepository;
  private final ReservationRepository reservationRepository;

  public List<SeatDTO> execute(GetSeatCommand command) {
    List<Seat> seats = seatRepository.findByConcertScheduleId(command.getConcertScheduleId());
    if (command.isAvailable()){
      List<Long> reservedSeatIds = reservationRepository.findReservedSeatIdByConcertScheduleId(command.getConcertScheduleId());
      return seats.stream()
          .filter(seat -> !reservedSeatIds.contains(seat.getId()))
          .map(SeatDTO::from)
          .collect(Collectors.toList());
    }
    else {
      return seats.stream()
          .map(SeatDTO::from)
          .collect(Collectors.toList());
    }
  }
}
