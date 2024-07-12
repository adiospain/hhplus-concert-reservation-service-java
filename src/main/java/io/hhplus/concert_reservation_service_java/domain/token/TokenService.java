  package io.hhplus.concert_reservation_service_java.domain.token;

  import io.hhplus.concert_reservation_service_java.application.token.service.TokenWithPosition;
  import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
  import java.time.LocalDateTime;
  import java.util.List;

  public interface TokenService {
    TokenWithPosition upsertToken(long userId);
    TokenWithPosition getToken(long userId);

    List<Token> findActiveExpiredTokens();

    int bulkUpdateExpiredTokens();

    List<Token> getExpiredTokens();

    void activateNextToken(Long id, LocalDateTime localDateTime);
  }
