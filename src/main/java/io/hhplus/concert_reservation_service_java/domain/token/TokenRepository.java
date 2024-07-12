package io.hhplus.concert_reservation_service_java.domain.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository {

  Optional<Token> findByUserId(long userId);

  Token save(Token token);

  Optional<Long> findSmallestActiveTokenId();

  List<Token> findActiveExpiredTokens(LocalDateTime now);


  int bulkUpdateExpiredTokens(LocalDateTime now);

  List<Token> findExpiredTokens(LocalDateTime now);

  void activateNextToken(Long tokenId, LocalDateTime expireAt);
}
