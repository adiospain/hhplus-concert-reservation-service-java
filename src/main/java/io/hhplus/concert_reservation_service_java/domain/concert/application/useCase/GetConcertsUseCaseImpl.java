package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertMapper;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetConcertsUseCaseImpl implements GetConcertsUseCase {

  private final ConcertRepository concertRepository;
  private final ConcertMapper concertMapper;

  @Override
  public List<ConcertDTO> execute() {
    List<Concert> concerts = concertRepository.findAll();

    return concertMapper.WithoutConcertScheduleFrom(concerts);
  }
}
