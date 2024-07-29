package io.hhplus.concert_reservation_service_java.integration.useCase.token;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.domain.token.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.IssueTokenCommand;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class IssueTokenUseCaseIntegrationTest {

  @Autowired
  private IssueTokenUseCase issueTokenUseCase;

  @Autowired
  private TokenRepository tokenRepository;

  @BeforeEach
  void setUp() {

  }

  @Test
  @DisplayName("토큰 발급 성공")
  void execute_ShouldReturnTokenDTO_WhenTokenIssuedSuccessfully() {
    // Given
    Long UserId = 1L;
    String accessKey = UUID.randomUUID().toString();
    IssueTokenCommand command = IssueTokenCommand.builder()
        .userId(UserId)
        .accessKey(accessKey)
        .build();

    // When
    TokenDomain result = issueTokenUseCase.execute(command);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(UserId);
    assertThat(result.getAccessKey()).isEqualTo(accessKey);

    // Verify token is saved in the database
    Token savedToken = tokenRepository.findByAccessKey(accessKey).orElseThrow();
    assertThat(savedToken).isNotNull();
    assertThat(savedToken.getUserId()).isEqualTo(UserId);
    assertThat(savedToken.getAccessKey()).isEqualTo(accessKey);
  }

  @Test
  @DisplayName("토큰 발급 실패")
  void execute_ShouldThrowException_WhenTokenServiceFails() {
    // Given
    Long UserId = 1L;
    String accessKey = UUID.randomUUID().toString();
    IssueTokenCommand command = IssueTokenCommand.builder()
        .userId(UserId)
        .accessKey(accessKey)
        .build();

    // Simulate a failure in the token service by deleting the token after creation
    tokenRepository.deleteAll();

    // When & Then
    assertThatThrownBy(() -> issueTokenUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SERVICE);

    // Verify no token is saved in the database
    assertThat(tokenRepository.findAll()).isEmpty();
  }
}