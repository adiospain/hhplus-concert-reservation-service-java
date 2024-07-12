package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.token.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenRepository;
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
  public Optional<Token> findByUserId(long userId) {
    return tokenRepository.findByUserId(userId);
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
  public int bulkUpdateExpiredTokens(LocalDateTime now) {
    return tokenRepository.bulkUpdateExpiredTokens(now);
  }

  @Override
  public List<Token> findExpiredTokens(LocalDateTime now) {
    return tokenRepository.findExpiredTokens(now);
  }

  @Override
  public void activateNextToken(Long tokenId, LocalDateTime expireAt) {
    tokenRepository.activateNextToken(tokenId, expireAt);
  }


}
