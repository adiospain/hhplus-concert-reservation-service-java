package io.hhplus.concert_reservation_service_java.domain.token.application.service;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {
  private final TokenRepository tokenRepository;

  @Override
  @Transactional
  public TokenDomain upsertToken(long reserverId) {

    Token token = tokenRepository.findByReserverId(reserverId)
        .map(existingToken -> {
          // 기존 토큰 업데이트
          existingToken.setStatus(TokenStatus.WAIT);
          existingToken.setAccessKey(UUID.randomUUID().toString());
          existingToken.setExpireAt(LocalDateTime.now().plusMinutes(5));
          existingToken.setUpdatedAt(LocalDateTime.now());
          return existingToken;
        })
        .orElseGet(() -> {
          // 새 토큰 생성
          return Token.builder()
              .reserverId(reserverId)
              .accessKey(UUID.randomUUID().toString())
              .status(TokenStatus.WAIT)
              .expireAt(LocalDateTime.now().plusMinutes(5))
              .build();
        });
    Token savedToken = tokenRepository.save(token);

    Long smallestActiveTokenId = tokenRepository.findSmallestActiveTokenId().orElse(savedToken.getId());
    long queuePosition = (savedToken.getId() - smallestActiveTokenId);

    if (queuePosition == 0) {
      savedToken.setStatus(TokenStatus.ACTIVE);
      tokenRepository.save(savedToken);
    }
    return new TokenDomain(token, queuePosition);
  }

  @Override
  @Transactional(readOnly = true)
  public TokenDomain getToken(long reserverId, String accessKey) {
    Token token = tokenRepository.findByAccessKey(accessKey)
        .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND)); //토큰이 존재하지 않는다면 예외처리
    if (token.getReserverId() != reserverId){
      throw new CustomException(ErrorCode.TOKEN_AND_USER_NOT_MATCHED);
    }
    Long smallestActiveTokenId = tokenRepository.findSmallestActiveTokenId().orElse(token.getId());
    long queuePosition = (token.getId() - smallestActiveTokenId);
    if (queuePosition == 0) {
      token.setStatus(TokenStatus.ACTIVE);
      tokenRepository.save(token);
    }
    return new TokenDomain(token, queuePosition);
  }

  @Override
  @Transactional
  public int bulkUpdateExpiredTokens() {
    return tokenRepository.bulkUpdateExpiredTokens(LocalDateTime.now());
  }

  @Override
  public int bulkUpdateDisconnectedToken() {
    return tokenRepository.bulkUpdateDisconnectedToken(LocalDateTime.now());
  }

  @Override
  @Transactional (readOnly = true)
  public List<Token> getExpiredTokens() {
    return tokenRepository.findExpiredTokens(LocalDateTime.now());
  }

  @Override
  @Transactional
  public void setTokenStatusToDone(long id) {
    tokenRepository.setTokenStatusToDone(id);
  }

  @Override
  @Transactional
  public int activateNextToken(long id){
    return tokenRepository.activateNextToken(id, LocalDateTime.now());
  }

  @Override
  public Optional<Token> findMostRecentlyDisconnectedToken() {
    return tokenRepository.findMostRecentlyDisconnectedToken();
  }

  @Override
  public void completeTokenAndActivateNextToken(long id) {
    this.setTokenStatusToDone(id);
    this.activateNextToken(id);
  }


}
