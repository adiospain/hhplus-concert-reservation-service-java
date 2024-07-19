package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class TokenRepositoryImpl implements TokenRepository {

  private final TokenJpaRepository tokenRepository;

  @Override
  public Optional<Token> findByAccessKey(String accessKey){
    return tokenRepository.findByAccessKey(accessKey);
  }



  @Override
  public Optional<Token> findMostRecentlyDisconnectedToken() {
    return tokenRepository.findMostRecentlyDisconnectedToken();
  }

  @Override
  public Optional<Token> findByReserverId(long reserverId) {
    return tokenRepository.findByReserverId(reserverId);
  }

  @Override
  public Token save(Token token) {
    return tokenRepository.save(token);
  }

  @Override
  public Optional<Long> findSmallestActiveTokenId() {
    return tokenRepository.findSmallestActiveTokenId();
  }

  @Override
  public List<Token> findActiveExpiredTokens(LocalDateTime now) {
    return tokenRepository.findActiveExpiredTokens(now);
  }

  @Override
  public long findLastActiveToken() {
    return tokenRepository.findLastActiveToken();
  }

  @Override
  public int bulkUpdateExpiredTokens(LocalDateTime now) {
    return tokenRepository.bulkUpdateExpiredTokens(now);
  }

  @Override
  public int bulkUpdateDisconnectedToken(LocalDateTime now) {
    LocalDateTime threshold = LocalDateTime.now().minusSeconds(5);
    return tokenRepository.bulkUpdateDisconnectedToken(threshold);
  }

  @Override
  public List<Token> findExpiredTokens(LocalDateTime now) {
    return tokenRepository.findExpiredTokens(now);
  }


  @Override
  public void setTokenStatusToDone(long id) {
    tokenRepository.setTokenStatusToDone(id);
  }

  @Override
  public int activateNextToken(Long tokenId, LocalDateTime expireAt) {
    return tokenRepository.activateNextToken(tokenId, expireAt);
  }


}
