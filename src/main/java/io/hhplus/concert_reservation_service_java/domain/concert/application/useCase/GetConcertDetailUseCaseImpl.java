package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetConcertDetailUseCaseImpl implements GetConcertDetailUseCase {

  private final ConcertService concertService;
  private final ConcertMapper concertMapper;

  @Override
  public ConcertDomain execute(GetConcertDetailCommand command) {
    List<ConcertSchedule> concertSchedules = concertService.getAllConcertSchedulesByConcertId(command.getConcertId());
    return concertMapper.WithConcertScheduleFrom(concertSchedules);
  }
}
