package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenUseCommand;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetTokenUseCaseImpl implements GetTokenUseCase {
  private final TokenService tokenService;

  @Override
  public TokenDomain execute(GetTokenUseCommand command) {
    TokenDomain tokenDomain = tokenService.getToken(command.getReserverId() ,command.getAccessKey());
    return tokenDomain;
  }
}
