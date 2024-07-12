package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.ChargePointCommand;

public interface ChargePointUseCase {

  int execute(ChargePointCommand command);
}
