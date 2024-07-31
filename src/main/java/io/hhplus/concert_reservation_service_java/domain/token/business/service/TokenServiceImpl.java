package io.hhplus.concert_reservation_service_java.domain.token.business.service;

import io.hhplus.concert_reservation_service_java.core.common.annotation.DistributedLock;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {
  private final TokenRepository tokenRepository;


  @Override
  @Transactional
  public TokenDomain upsertToken(long reserverId, String accessKey) {
    if (accessKey.isEmpty()){
      throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
    }
    Token token = tokenRepository.findByUserIdAndAccessKey(reserverId, accessKey)
        .map(existingToken -> {
          // 기존 토큰 업데이트
          if (existingToken.getStatus() == TokenStatus.DONE |
              existingToken.getStatus() == TokenStatus.DISCONNECTED |
              existingToken.getStatus() == TokenStatus.EXPIRED)
          {
            existingToken.renew();
          }
          return existingToken;
        })
        .orElseGet(() -> {
          // 새 토큰 생성
          return Token.createWaitingToken(reserverId);
        });
    Token savedToken = tokenRepository.save(token);
    return new TokenDomain(savedToken, token.getPosition());
  }

  @Override
  public TokenDomain getToken(long userId, String accessKey) {
    Token token = tokenRepository.findByUserIdAndAccessKey(userId,accessKey)
        .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND)); //토큰이 존재하지 않는다면 예외처리
    return new TokenDomain(token, token.getPosition());
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
  public int activateNextToken(long id) {
    LocalDateTime now = LocalDateTime.now();
    return tokenRepository.activateNextToken(id, now);
  }

  @Override
  @Transactional
  public void activateNextToken() {
    tokenRepository.findLastActiveToken()
        .ifPresent(lastActiveTokenId -> {
            LocalDateTime now = LocalDateTime.now();
            tokenRepository.activateNextToken(lastActiveTokenId, now);
    });
  }

  @Override
  public Token getTokenByAccessKey(String accessKey) {
    return tokenRepository.findByAccessKey(accessKey)
        .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND));
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
