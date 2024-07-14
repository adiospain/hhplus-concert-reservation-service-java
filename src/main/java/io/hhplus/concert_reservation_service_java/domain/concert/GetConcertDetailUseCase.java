package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;


public interface GetConcertDetailUseCase {

  ConcertDomain execute(GetConcertDetailCommand command);
}
