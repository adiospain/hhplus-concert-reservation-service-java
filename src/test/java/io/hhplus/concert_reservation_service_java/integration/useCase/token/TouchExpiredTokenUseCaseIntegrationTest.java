package io.hhplus.concert_reservation_service_java.integration.useCase.token;

import io.hhplus.concert_reservation_service_java.domain.token.TouchExpiredTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TouchExpiredTokenUseCaseIntegrationTest {

  @Autowired
  private TouchExpiredTokenUseCase touchExpiredTokenUseCase;

  @Autowired
  private TokenRepository tokenRepository;

  @BeforeEach
  void setUp() {
    tokenRepository.deleteAll();
  }

  @Test
  void execute_ShouldUpdateExpiredTokens() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    Token expiredToken1 = createToken(TokenStatus.ACTIVE, now.minusMinutes(16));
    Token expiredToken2 = createToken(TokenStatus.ACTIVE, now.minusMinutes(20));
    Token activeToken = createToken(TokenStatus.ACTIVE, now.minusMinutes(5));

    // When
    touchExpiredTokenUseCase.execute();

    // Then
    List<Token> updatedTokens = tokenRepository.findAll();
    assertThat(updatedTokens).hasSize(3);
    assertThat(updatedTokens).filteredOn(token -> token.getStatus() == TokenStatus.EXPIRED).hasSize(2);
    assertThat(updatedTokens).filteredOn(token -> token.getStatus() == TokenStatus.ACTIVE).hasSize(1);
  }

  @Test
  void execute_WhenNoExpiredTokens_ShouldNotUpdateAnyTokens() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    Token activeToken1 = createToken(TokenStatus.ACTIVE, now.minusMinutes(5));
    Token activeToken2 = createToken(TokenStatus.ACTIVE, now.minusMinutes(10));

    // When
    touchExpiredTokenUseCase.execute();

    // Then
    List<Token> updatedTokens = tokenRepository.findAll();
    assertThat(updatedTokens).hasSize(2);
    assertThat(updatedTokens).allMatch(token -> token.getStatus() == TokenStatus.ACTIVE);
  }

  private Token createToken(TokenStatus status, LocalDateTime createdAt) {
    Token token = Token.builder()
            .status(status)
            .createdAt(createdAt)
            .build();
    return tokenRepository.save(token);
  }
}
