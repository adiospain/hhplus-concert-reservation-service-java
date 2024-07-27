package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;


@RequiredArgsConstructor
@UseCase
public class GetPointUseCaseImpl implements GetPointUseCase {
  private final UserService userService;

  @Override
  public int execute(GetPointCommand command) {
    try{
      return userService.getPoint(command.getReserverId());
    }
    catch (PessimisticLockingFailureException e) {
      throw new CustomException(ErrorCode.CONCURRENT_LOCK);
    }
  }
}
