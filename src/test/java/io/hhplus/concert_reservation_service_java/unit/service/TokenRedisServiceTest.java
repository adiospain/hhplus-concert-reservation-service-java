package io.hhplus.concert_reservation_service_java.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.business.service.TokenServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepositoryImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TokenRedisServiceTest {

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private TokenServiceImpl tokenService;

  @BeforeEach
  void setUp() {
    tokenService = new TokenServiceImpl(tokenRepository);
  }

  @Test
  @DisplayName("upsertToken::이미 존재하는 토큰 없으면 새로운 토큰 생성")
  void upsertToken_WhenTokenDoesNotExist_ShouldCreateNewToken() {
    // Given
    long reserverId = 6L;
    String accessKey = "newAccessKey";

    Token token = Token.builder()
        .id(2L)
        .userId(reserverId)
        .accessKey(UUID.randomUUID().toString())
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();
    // When
    TokenDomain result = tokenService.upsertToken(reserverId, accessKey);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getQueuePosition());
  }

  @Test
  @DisplayName("getToken::토큰 성공")
  void getToken() {
    // Given
    long reserverId = 1L;
    String accessKey = "newAccessKey";

    Token token = Token.builder()
        .id(2L)
        .userId(reserverId)
        .accessKey(accessKey)
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();
    // When
    TokenDomain upsertResult = tokenService.upsertToken(reserverId, accessKey);

    TokenDomain getResult = tokenService.getTokenByAccessKey(upsertResult.getAccessKey());
    // Then
    assertNotNull(getResult);
    assertEquals(1L, getResult.getQueuePosition());
  }
}
