package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.application.concert.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;


public interface GetConcertDetailUseCase {

  ConcertDTO execute(GetConcertDetailCommand command);
}
