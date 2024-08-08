package io.hhplus.concert_reservation_service_java.domain.user;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.UsePointCommand;

public interface UsePointUseCase {
  int execute(UsePointCommand command);
}
