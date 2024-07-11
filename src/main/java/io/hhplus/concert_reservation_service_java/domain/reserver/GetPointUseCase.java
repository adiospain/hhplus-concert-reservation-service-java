package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.application.reserver.port.in.GetPointCommand;

public interface GetPointUseCase {

  int execute(GetPointCommand command);
}
