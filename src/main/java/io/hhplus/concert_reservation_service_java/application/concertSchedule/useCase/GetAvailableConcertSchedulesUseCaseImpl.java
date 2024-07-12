package io.hhplus.concert_reservation_service_java.application.concertSchedule.useCase;

import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.GetAvailableConcertSchedulesUseCase;
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
