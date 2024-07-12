package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.GetPointCommand;

public interface GetPointUseCase {

  int execute(GetPointCommand command);
}
