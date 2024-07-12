package io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.GetPointCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@UseCase
public class GetPointUseCaseImpl implements GetPointUseCase {

  private final ReserverRepository reserverRepository;

  public int execute(GetPointCommand command) {
    Reserver reserver = reserverRepository.findById(command.getUserId())
        .orElseThrow(()->new CustomException(ErrorCode.RESERVER_NOT_FOUND));
    return reserver.getPoint();
  }
}
