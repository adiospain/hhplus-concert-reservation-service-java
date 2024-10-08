package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;

@RequiredArgsConstructor
@UseCase
@Slf4j
public class ChargePointUseCaseImpl implements ChargePointUseCase {
  private final UserService userService;

  @Override
  public int execute(ChargePointCommand command) {
    try{
      User user = userService.chargePoint(command.getUserId(), command.getAmount());
      return user.getPoint();
    }
    catch (PessimisticLockingFailureException e){
      throw new CustomException(ErrorCode.CONCURRENT_LOCK);
    }
  }
}
