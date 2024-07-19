package io.hhplus.concert_reservation_service_java.domain.concert.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConcertScheduleSeatMapper {

    public ConcertScheduleSeatDomain from (ConcertScheduleSeat concertScheduleSeat){
      if (concertScheduleSeat == null){
        throw new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND);
      }
      return ConcertScheduleSeatDomain.builder()
          .id(concertScheduleSeat.getId())
          .seatNumber(concertScheduleSeat.getSeat().getSeatNumber())
          .build();
    }

    public List<ConcertScheduleSeatDomain> from (List<ConcertScheduleSeat> concertScheduleSeat){
      if (concertScheduleSeat == null || concertScheduleSeat.isEmpty()){
        throw new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND);
      }
      return concertScheduleSeat.stream()
          .map(this::from)
          .collect(Collectors.toList());
    }

    public ConcertScheduleSeatDomain from (Seat seat){
      return ConcertScheduleSeatDomain.builder()
          .id(seat.getId())
          .seatNumber(seat.getSeatNumber())
          .build();
    }

    public List<ConcertScheduleSeatDomain> AvailableSeatsFrom(List<Seat> allSeats, Set<Long> reservedSeatIds) {
      return allSeats.stream()
          .filter(seat -> !reservedSeatIds.contains(seat.getId()))
          .map(this::from)
          .collect(Collectors.toList());
    }
}
