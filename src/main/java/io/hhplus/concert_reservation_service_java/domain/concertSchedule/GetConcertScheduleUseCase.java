package io.hhplus.concert_reservation_service_java.domain.concertSchedule;


import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetConcertScheduleCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.concertSchedule.dto.ConcertScheduleDTO;
import java.util.List;

public interface GetConcertScheduleUseCase {

  List<ConcertScheduleDTO> execute(GetConcertScheduleCommand command);
}
