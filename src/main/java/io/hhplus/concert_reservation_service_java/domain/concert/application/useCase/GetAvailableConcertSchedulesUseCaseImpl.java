package io.hhplus.concert_reservation_service_java.domain.concert.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleMapper;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.ConcertSchedule;

import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetAvailableConcertSchedulesUseCaseImpl implements
    GetAvailableConcertSchedulesUseCase {

  private final ConcertRepository concertRepository;
  private final ConcertScheduleMapper concertScheduleMapper;

  public List<ConcertScheduleDTO> execute(GetAvailableConcertSchedulesCommand command) {
    List<ConcertSchedule> schedules = concertRepository.findUpcomingConcertSchedules(command.getConcertId(), LocalDateTime.now());
    return concertScheduleMapper.from(schedules);
  }
}
