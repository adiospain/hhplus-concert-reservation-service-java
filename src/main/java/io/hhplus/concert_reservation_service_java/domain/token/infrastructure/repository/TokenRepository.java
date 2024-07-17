package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository {

  Optional <Token> findMostRecentlyDisconnectedToken();

  Optional<Token> findByReserverId(long reserverId);

  Token save(Token token);

  Optional<Long> findSmallestActiveTokenId();

  List<Token> findActiveExpiredTokens(LocalDateTime now);

  int bulkUpdateExpiredTokens(LocalDateTime now);
  int bulkUpdateDisconnectedToken(LocalDateTime now);

  List<Token> findExpiredTokens(LocalDateTime now);

  int activateNextToken(Long tokenId, LocalDateTime expireAt);

  Optional<Token> findByAccessKey(String accessKey);

  void setTokenStatusToDone(long id);
}
