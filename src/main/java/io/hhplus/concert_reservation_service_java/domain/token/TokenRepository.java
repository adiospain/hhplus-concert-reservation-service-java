package io.hhplus.concert_reservation_service_java.domain.token;

import java.util.Optional;

public interface TokenRepository {

  Optional<Token> findByUserId(long userId);

  Token save(Token token);

  Optional<Long> findSmallestActiveTokenId();
}
