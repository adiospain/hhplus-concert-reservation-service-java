package io.hhplus.concert_reservation_service_java.domain.user;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;

public interface ChargePointUseCase {

  int execute(ChargePointCommand command);
}
