package io.hhplus.concert_reservation_service_java.domain.token.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenCommand;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.token.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetTokenUseCaseImpl implements GetTokenUseCase {
  private final TokenService tokenService;

  @Override
  public TokenDomain execute(GetTokenCommand command) {
    TokenDomain tokenDomain = tokenService.getToken(command.getUserId() ,command.getAccessKey());
    return tokenDomain;
  }
}
