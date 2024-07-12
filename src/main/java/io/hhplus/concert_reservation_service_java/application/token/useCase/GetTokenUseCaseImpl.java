package io.hhplus.concert_reservation_service_java.application.token.useCase;

import io.hhplus.concert_reservation_service_java.application.token.port.in.GetTokenUseCommand;
import io.hhplus.concert_reservation_service_java.application.token.service.TokenWithPosition;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetTokenUseCaseImpl implements GetTokenUseCase {
  private final TokenService tokenService;
  private final TokenMapper tokenMapper;
  @Override
  public TokenDTO execute(GetTokenUseCommand command) {
    TokenWithPosition tokenWithPosition = tokenService.getToken(command.getReserverId());
    return tokenMapper.from(tokenWithPosition.getToken(), tokenWithPosition.getQueuePosition());
  }
}
