package io.hhplus.concert_reservation_service_java.domain.token.application.service;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverJpaRepository;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {
  private final ReserverJpaRepository reserverRepository;
  private final TokenJpaRepository tokenRepository;

  @Override
  public TokenWithPosition upsertToken(long reserverId) {
    Reserver reserver = reserverRepository.findById(reserverId)
        .orElseThrow(()->new CustomException(ErrorCode.RESERVER_NOT_FOUND));
    Token token = tokenRepository.findByUserId(reserverId)
        .map(existingToken -> {
          // 기존 토큰 업데이트
          existingToken.setStatus(TokenStatus.WAIT);
          existingToken.setExpireAt(LocalDateTime.now().plusMinutes(5));
          existingToken.setUpdatedAt(LocalDateTime.now());
          return existingToken;
        })
        .orElseGet(() -> {
          // 새 토큰 생성
          return Token.builder()
              .userId(reserverId)
              .status(TokenStatus.WAIT)
              .expireAt(LocalDateTime.now().plusMinutes(5))
              .build();
        });
    Token savedToken = tokenRepository.save(token);

    Long smallestActiveTokenId = tokenRepository.findSmallestActiveTokenId().orElse(savedToken.getId());
    long queuePosition = (savedToken.getId() - smallestActiveTokenId + 1);

    if (queuePosition == 1) {
      savedToken.setStatus(TokenStatus.ACTIVE);
      tokenRepository.save(savedToken);
    }
    return new TokenWithPosition(token, queuePosition);
  }

  @Override
  public TokenWithPosition getToken(long reserverId) {
    Long smallestActiveTokenId = tokenRepository.findSmallestActiveTokenId().orElse(reserverId);
    long queuePosition = (reserverId - smallestActiveTokenId + 1);
    Token token = tokenRepository.findByUserId(reserverId)
        .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND)); // 토큰 존재하지 않는다면 새로 발급
    if (queuePosition == 1) {
      token.setStatus(TokenStatus.ACTIVE);
      tokenRepository.save(token);
    }
    return new TokenWithPosition(token, queuePosition);
  }

  @Override
  public List<Token> findActiveExpiredTokens() {
    return tokenRepository.findActiveExpiredTokens(LocalDateTime.now());
  }

  @Override
  @Transactional
  public int bulkUpdateExpiredTokens() {
    return tokenRepository.bulkUpdateExpiredTokens(LocalDateTime.now());
  }

  @Override
  @Transactional (readOnly = true)
  public List<Token> getExpiredTokens() {
    return tokenRepository.findExpiredTokens(LocalDateTime.now());
  }

  @Override
  @Transactional
  public void activateNextToken(Long tokenId, LocalDateTime expireAt) {
    tokenRepository.activateNextToken(tokenId, expireAt);
  }
}
