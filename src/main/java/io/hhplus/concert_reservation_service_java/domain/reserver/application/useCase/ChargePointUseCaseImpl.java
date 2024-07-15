package io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class ChargePointUseCaseImpl implements ChargePointUseCase {
  private final ReserverRepository reserverRepository;

  @Override
  @Transactional
  public int execute(ChargePointCommand command) {
    Reserver reserver = reserverRepository.findByIdWithPessimisticLock(command.getUserId())
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));

    if (command.getAmount() <= 0) {
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }

    reserver.chargePoint(command.getAmount());

    reserverRepository.save(reserver);

    return reserver.getPoint();
  }
}
