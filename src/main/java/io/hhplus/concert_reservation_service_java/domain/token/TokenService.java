package io.hhplus.concert_reservation_service_java.domain.token;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.util.List;
import java.util.Optional;

public interface TokenService {
  TokenDomain upsertToken(long userId);

  TokenDomain getToken(long reserverId, String accessKey);

  int bulkUpdateExpiredTokens();
  int bulkUpdateDisconnectedToken();

  List<Token> getExpiredTokens();

  Optional<Token> findMostRecentlyDisconnectedToken();




  void completeTokenAndActivateNextToken(long id);
    void setTokenStatusToDone(long id);
    int activateNextToken(long id);
}
