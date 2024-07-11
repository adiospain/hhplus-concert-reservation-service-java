package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;

public interface GetConcertsUseCase {

  List<ConcertDTO> execute();
}
