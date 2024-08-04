package io.hhplus.concert_reservation_service_java.domain.token.business.service;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
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
  public TokenDomain upsertToken(long userId, String accessKey) {


    if (accessKey.isEmpty()){
      Token savedToken = tokenRepository.save(Token.createWaitingToken(userId));
      return new TokenDomain(savedToken);
    }

    Token token = tokenRepository.findByUserIdAndAccessKey(userId, accessKey)
        .map(existingToken -> {
            existingToken.renew(userId);
          // 업데이트
          return existingToken;
        })
        .orElseGet(() -> {
          // 새 토큰 생성
          return Token.createWaitingToken(userId);
        });
    Token savedToken = tokenRepository.save(token);

    return new TokenDomain(savedToken);
  }

  @Override
  public TokenDomain getToken(long userId, String accessKey) {
    Token token = tokenRepository.findByUserIdAndAccessKey(userId,accessKey)
        .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND)); //토큰이 존재하지 않는다면 예외처리
    return new TokenDomain(token);
  }

  @Override
  public TokenDomain getToken(String accessKey) {
    Token token = tokenRepository.findByAccessKey(accessKey)
        .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    return new TokenDomain(token);
  }

    @Override
    public void touchExpiredTokens() {tokenRepository.touchExpiredTokens();}
    @Override
    public void activateNextTokens() {tokenRepository.activateTokens();}

}
