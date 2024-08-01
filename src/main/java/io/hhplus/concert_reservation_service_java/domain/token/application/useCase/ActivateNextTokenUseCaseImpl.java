package io.hhplus.concert_reservation_service_java.domain.token.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.token.ActivateNextTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class ActivateNextTokenUseCaseImpl implements ActivateNextTokenUseCase {
  private final TokenService tokenService;

  @Override
  public void execute() {
    tokenService.activateNextTokens();
  }
}
