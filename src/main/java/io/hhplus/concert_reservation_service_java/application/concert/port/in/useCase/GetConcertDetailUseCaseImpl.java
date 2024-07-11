package io.hhplus.concert_reservation_service_java.application.concert.port.in.useCase;

import io.hhplus.concert_reservation_service_java.application.concert.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetConcertDetailUseCaseImpl implements GetConcertDetailUseCase {

  private final ConcertRepository concertRepository;
  private final ConcertMapper concertMapper;

  @Override
  public ConcertDTO execute(GetConcertDetailCommand command) {
    List<ConcertSchedule> concertSchedules = concertRepository.findAllConcertSchedulesByConcertId(command.getConcertId());
    return concertMapper.WithConcertScheduleFrom(concertSchedules);
  }
}
