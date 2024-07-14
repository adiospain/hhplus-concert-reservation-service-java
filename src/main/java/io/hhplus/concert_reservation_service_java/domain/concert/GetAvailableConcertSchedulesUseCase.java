package io.hhplus.concert_reservation_service_java.domain.concert;



import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import java.util.List;

public interface GetAvailableConcertSchedulesUseCase {

  List<ConcertScheduleDomain> execute(GetAvailableConcertSchedulesCommand command);
}
