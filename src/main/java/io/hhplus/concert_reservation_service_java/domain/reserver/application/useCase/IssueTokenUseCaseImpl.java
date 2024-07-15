package io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.domain.token.application.service.TokenWithPosition;
import io.hhplus.concert_reservation_service_java.domain.token.application.port.out.TokenMapper;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class IssueTokenUseCaseImpl implements IssueTokenUseCase {

  private final TokenService tokenService;
  private final TokenMapper tokenMapper;

  @Override
  public TokenDomain execute(IssueTokenUseCommand command) {
    TokenWithPosition tokenWithPosition = tokenService.upsertToken(command.getReserverId());
    return tokenMapper.from(tokenWithPosition.getToken(), tokenWithPosition.getQueuePosition());
  }
}
