package io.hhplus.concert_reservation_service_java.application.token.useCase;

import io.hhplus.concert_reservation_service_java.application.token.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.application.token.service.TokenWithPosition;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenRepository;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.TokenStatus;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class IssueTokenUseCaseImpl implements IssueTokenUseCase {

  private final TokenService tokenService;
  private final TokenMapper tokenMapper;

  @Override
  public TokenDTO execute(IssueTokenUseCommand command) {
    TokenWithPosition tokenWithPosition = tokenService.upsertToken(command.getReserverId());
    return tokenMapper.from(tokenWithPosition.getToken(), tokenWithPosition.getQueuePosition());
  }
}
