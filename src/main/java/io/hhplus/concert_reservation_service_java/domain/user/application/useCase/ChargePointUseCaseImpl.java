package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
