package io.hhplus.concert_reservation_service_java.domain.token;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.application.service.TokenWithPosition;
import java.time.LocalDateTime;
import java.util.List;

public interface TokenService {
  TokenWithPosition upsertToken(long userId);

  TokenWithPosition getToken(long reserverId, String accessKey);

  List<Token> findActiveExpiredTokens();

  int bulkUpdateExpiredTokens();

  List<Token> getExpiredTokens();

  void activateNextToken(Long id, LocalDateTime localDateTime);

  void activateNextToken();
}
