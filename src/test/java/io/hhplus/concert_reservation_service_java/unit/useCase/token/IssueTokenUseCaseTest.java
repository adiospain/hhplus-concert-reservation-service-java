package io.hhplus.concert_reservation_service_java.unit.useCase.token;

import static org.junit.jupiter.api.Assertions.*;


import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.domain.token.application.useCase.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.application.useCase.IssueTokenUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.mockito.Mockito.*;

class IssueTokenUseCaseTest {
  private TokenService tokenService = Mockito.mock(TokenService.class);
  private IssueTokenUseCase useCase = new IssueTokenUseCaseImpl(tokenService);

  @Test
  @DisplayName("토큰 발급 성공")
  void execute_ShouldReturnTokenDTO_WhenTokenIssuedSuccessfully() {

    Long reserverId = 1L;
    String accessKey = UUID.randomUUID().toString();
    IssueTokenUseCommand command = IssueTokenUseCommand.builder()
        .userId(reserverId)
        .accessKey(accessKey)
        .build();
    Token token = Token.createWaitingToken(reserverId);
    token.setIdForTest(2L);
    int queuePosition = 5;
    TokenDomain expectedTokenDomain = new TokenDomain(token, queuePosition);


    when(tokenService.upsertToken(reserverId, accessKey)).thenReturn(expectedTokenDomain);

    // Act
    TokenDomain result = useCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(expectedTokenDomain, result);
    verify(tokenService).upsertToken(reserverId, accessKey);
  }

  @Test
  @DisplayName("토큰 발급 실패")
  void execute_ShouldThrowException_WhenTokenServiceFails() {
    Long reserverId = 1L;
    String accessKey = UUID.randomUUID().toString();
    IssueTokenUseCommand command = IssueTokenUseCommand.builder()
            .userId(reserverId)
        .accessKey(accessKey)
                .build();

    when(tokenService.upsertToken(reserverId, accessKey)).thenThrow(new RuntimeException("Service failed"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> useCase.execute(command));
    verify(tokenService).upsertToken(reserverId, accessKey);
  }
}