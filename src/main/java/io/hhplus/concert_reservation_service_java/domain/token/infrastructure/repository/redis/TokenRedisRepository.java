package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.redis;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.util.List;
import java.util.Optional;

public interface TokenRedisRepository {

  Token save(Token token);

  Optional<Token> getToken(long userId, String accessKey);

  void deleteAll();

  List<Token> findAll();
  List<Token> findWaitingTokens();
  List<Token> findActiveTokens();

  void touchExpiredTokens();
  void activateTokens();




}
