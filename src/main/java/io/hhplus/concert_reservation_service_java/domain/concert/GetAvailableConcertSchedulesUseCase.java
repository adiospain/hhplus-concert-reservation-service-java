package io.hhplus.concert_reservation_service_java.domain.concert;



import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;

public interface GetAvailableConcertSchedulesUseCase {

  List<ConcertScheduleDTO> execute(GetAvailableConcertSchedulesCommand command);
}
