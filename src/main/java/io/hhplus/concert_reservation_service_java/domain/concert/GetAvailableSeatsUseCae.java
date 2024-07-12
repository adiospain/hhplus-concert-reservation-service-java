package io.hhplus.concert_reservation_service_java.domain.concert;


import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;


import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.List;

public interface GetAvailableSeatsUseCae {

  List<ConcertScheduleSeatDTO> execute(GetAvailableSeatsCommand command);
}
