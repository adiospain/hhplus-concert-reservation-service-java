package io.hhplus.concert_reservation_service_java.domain.user;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateUserCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;

public interface CreateUserUseCase {
  long execute(CreateUserCommand command);
}
