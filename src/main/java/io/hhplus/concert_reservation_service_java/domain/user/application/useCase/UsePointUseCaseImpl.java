package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UsePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.UsePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;

@RequiredArgsConstructor
@UseCase
public class UsePointUseCaseImpl implements UsePointUseCase {
  private final UserService userService;

  @Override
  public int execute(UsePointCommand command) {
    try{
      User user = userService.usePoint(command.getUserId(), command.getAmount());
      return user.getPoint();
    }
    catch (PessimisticLockingFailureException e){
      throw new CustomException(ErrorCode.CONCURRENT_LOCK);
    }
  }
}
