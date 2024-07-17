package io.hhplus.concert_reservation_service_java.domain.user;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;

public interface GetPointUseCase {

  int execute(GetPointCommand command);
}
