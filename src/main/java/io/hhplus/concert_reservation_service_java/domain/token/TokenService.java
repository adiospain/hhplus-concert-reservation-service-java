package io.hhplus.concert_reservation_service_java.domain.token;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface TokenService {

  TokenDomain upsertToken(long userId, String accessKey);
  TokenDomain getToken(long userId, String accessKey);
  TokenDomain getToken(String accessKey);
  void touchExpiredTokens();
  void activateNextTokens();

  TokenDomain getTokenByAccessKey(String tokenText);


}
