package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.IssueTokenUseCommand;

import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.token.application.useCase.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class IssueTokenUseCaseImpl implements IssueTokenUseCase {

  private final TokenService tokenService;

  @Override
  public TokenDomain execute(IssueTokenUseCommand command) {
    TokenDomain tokenDomain = tokenService.upsertToken(command.getUserId(),
        command.getAccessKey());
    return tokenDomain;
  }
}
