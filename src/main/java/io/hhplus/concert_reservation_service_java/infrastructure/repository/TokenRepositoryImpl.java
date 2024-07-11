package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.token.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.TokenJpaRepository;
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


}
