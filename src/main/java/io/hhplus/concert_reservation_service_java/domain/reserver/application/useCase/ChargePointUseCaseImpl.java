package io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.reserver.UserService;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.ChargePointUseCase;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class ChargePointUseCaseImpl implements ChargePointUseCase {
  private final UserService userService;

  @Override
  public int execute(ChargePointCommand command) {
    User user = userService.chargePoint(command.getUserId(), command.getAmount());
    return user.getPoint();
  }
}
