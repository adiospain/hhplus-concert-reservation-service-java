package io.hhplus.concert_reservation_service_java.application.concertScheduleSeat.useCase;

import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

  @Component
  public class ConcertScheduleSeatMapper {

    public ConcertScheduleSeatDTO from (ConcertScheduleSeat concertScheduleSeat){
      if (concertScheduleSeat == null){
        throw new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND);
      }
      return ConcertScheduleSeatDTO.builder()
          .id(concertScheduleSeat.getId())
          .seatNumber(concertScheduleSeat.getSeat().getSeatNumber())
          .build();
    }

    public List<ConcertScheduleSeatDTO> from (List<ConcertScheduleSeat> concertScheduleSeat){
      if (concertScheduleSeat == null || concertScheduleSeat.isEmpty()){
        throw new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND);
      }
      return concertScheduleSeat.stream()
          .map(this::from)
          .collect(Collectors.toList());
    }

    public ConcertScheduleSeatDTO from (Seat seat){
      return ConcertScheduleSeatDTO.builder()
          .id(seat.getId())
          .seatNumber(seat.getSeatNumber())
          .build();
    }

    public List<ConcertScheduleSeatDTO> AvailableSeatsFrom(List<Seat> allSeats, Set<Long> reservedSeatIds) {
      return allSeats.stream()
          .filter(seat -> !reservedSeatIds.contains(seat.getId()))
          .map(this::from)
          .collect(Collectors.toList());
    }
  }
