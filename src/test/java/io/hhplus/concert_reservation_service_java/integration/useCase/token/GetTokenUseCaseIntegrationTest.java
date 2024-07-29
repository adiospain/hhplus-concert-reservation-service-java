package io.hhplus.concert_reservation_service_java.integration.useCase.token;



import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenCommand;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.domain.user.GetTokenUseCase;
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
class GetTokenUseCaseIntegrationTest {

  @Autowired
  private GetTokenUseCase getTokenUseCase;

  @Autowired
  private TokenRepository tokenRepository;

  private Token token;

  @BeforeEach
  void setUp() {
    token = Token.builder()
            .id(1L)
            .userId(1L)
            .accessKey(UUID.randomUUID().toString())
            .status(TokenStatus.WAIT)
            .build();
    token = tokenRepository.save(token);
  }

  @Test
  @DisplayName("토큰 조회 성공")
  void execute_ShouldReturnTokenDTO_WhenTokenExists() {
    // Arrange
    GetTokenCommand command = GetTokenCommand.builder()
        .userId(token.getUserId())
        .accessKey(token.getAccessKey())
        .build();

    // Act
    TokenDomain result = getTokenUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(token.getUserId());
    assertThat(result.getAccessKey()).isEqualTo(token.getAccessKey());
  }

  @Test
  @DisplayName("존재하지 않는 토큰 조회 시 예외 발생")
  void execute_ShouldThrowException_WhenTokenDoesNotExist() {
    // Arrange
    GetTokenCommand command = GetTokenCommand.builder()
        .userId(999L)
        .accessKey("non-existent-access-key")
        .build();

    // Act & Assert
    assertThatThrownBy(() -> getTokenUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_NOT_FOUND);
  }

  @Test
  @DisplayName("잘못된 AccessKey로 토큰 조회 시 예외 발생")
  void execute_ShouldThrowException_WhenAccessKeyIsInvalid() {
    // Arrange
    GetTokenCommand command = GetTokenCommand.builder()
        .userId(token.getUserId())
        .accessKey("invalid-access-key")
        .build();

    // Act & Assert
    assertThatThrownBy(() -> getTokenUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
  }
}