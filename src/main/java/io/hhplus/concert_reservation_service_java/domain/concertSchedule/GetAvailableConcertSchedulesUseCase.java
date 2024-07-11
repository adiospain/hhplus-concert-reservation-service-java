package io.hhplus.concert_reservation_service_java.domain.concertSchedule;



import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;

public interface GetAvailableConcertSchedulesUseCase {

  List<ConcertScheduleDTO> execute(GetAvailableConcertSchedulesCommand command);
}
