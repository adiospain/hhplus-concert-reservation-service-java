package io.hhplus.concert_reservation_service_java.application.token.port.in.useCase;

import io.hhplus.concert_reservation_service_java.application.token.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenRepository;
import io.hhplus.concert_reservation_service_java.domain.token.TokenStatus;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class IssueTokenUseCaseImpl implements IssueTokenUseCase {

  private final ReserverRepository reserverRepository;
  private final TokenRepository tokenRepository;
  private final TokenMapper tokenMapper;

  @Override
  public TokenDTO execute(IssueTokenUseCommand command) {
    Reserver reserver = reserverRepository.findById(command.getUserId())
        .orElseThrow(()->new CustomException(ErrorCode.RESERVER_NOT_FOUND));
    Token token = tokenRepository.findByUserId(command.getUserId())
        .map(existingToken -> {
          // 기존 토큰 업데이트
          existingToken.setStatus(TokenStatus.WAIT);
          existingToken.setExpireAt(LocalDateTime.now().plusMinutes(5));
          return existingToken;
        })
        .orElseGet(() -> {
          // 새 토큰 생성
          return Token.builder()
              .userId(command.getUserId())
              .status(TokenStatus.WAIT)
              .expireAt(LocalDateTime.now().plusMinutes(5))
              .build();
        });
    Token savedToken = tokenRepository.save(token);

    Long smallestActiveTokenId = tokenRepository.findSmallestActiveTokenId().orElse(savedToken.getId());
    long queuePosition = (savedToken.getId() - smallestActiveTokenId + 1);
    return tokenMapper.from(token, queuePosition);
  }
}
