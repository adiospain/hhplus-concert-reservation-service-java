package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleMapper;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;

import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableConcertSchedulesUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@UseCase
public class GetAvailableConcertSchedulesUseCaseImpl implements
    GetAvailableConcertSchedulesUseCase {
  private final ConcertService concertService;
  private final ConcertScheduleMapper concertScheduleMapper;

  public List<ConcertScheduleDomain> execute(GetAvailableConcertSchedulesCommand command) {
    List<ConcertSchedule> scheduleDomains = concertService.getUpcomingConcertSchedules(command.getConcertId());
    return concertScheduleMapper.from(scheduleDomains);
  }
}
