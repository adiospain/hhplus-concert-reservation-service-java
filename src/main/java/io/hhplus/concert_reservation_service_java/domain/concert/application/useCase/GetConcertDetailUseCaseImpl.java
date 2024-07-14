package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetConcertDetailUseCaseImpl implements GetConcertDetailUseCase {

  private final ConcertRepository concertRepository;
  private final ConcertMapper concertMapper;

  @Override
  public ConcertDomain execute(GetConcertDetailCommand command) {
    List<ConcertSchedule> concertSchedules = concertRepository.findAllConcertSchedulesByConcertId(command.getConcertId());
    return concertMapper.WithConcertScheduleFrom(concertSchedules);
  }
}
