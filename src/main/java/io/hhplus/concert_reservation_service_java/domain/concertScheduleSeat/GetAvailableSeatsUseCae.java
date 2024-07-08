package io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat;


import io.hhplus.concert_reservation_service_java.application.concertScheduleSeat.port.in.GetAvailableSeatsCommand;


import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.List;

public interface GetAvailableSeatsUseCae {

  List<ConcertScheduleSeatDTO> execute(GetAvailableSeatsCommand command);
}
