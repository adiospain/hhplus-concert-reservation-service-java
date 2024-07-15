package io.hhplus.concert_reservation_service_java.application.token.useCase;

import static org.junit.jupiter.api.Assertions.*;


import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.domain.token.application.service.TokenWithPosition;
import io.hhplus.concert_reservation_service_java.domain.reserver.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.port.out.TokenMapper;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase.IssueTokenUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.mockito.Mockito.*;

class IssueTokenUseCaseTest {
  private TokenService tokenService = Mockito.mock(TokenService.class);
  private TokenMapper tokenMapper = Mockito.mock(TokenMapper.class);
  private IssueTokenUseCase useCase = new IssueTokenUseCaseImpl(tokenService, tokenMapper);

  @Test
  void execute_ShouldReturnTokenDTO_WhenTokenIssuedSuccessfully() {

    Long reserverId = 1L;
    IssueTokenUseCommand command = IssueTokenUseCommand.builder()
        .reserverId(reserverId)
        .build();
    Token token = new Token();
    int queuePosition = 5;
    TokenWithPosition tokenWithPosition = new TokenWithPosition(token, queuePosition);
    TokenDomain expectedTokenDomain = new TokenDomain();

    when(tokenService.upsertToken(reserverId)).thenReturn(tokenWithPosition);
    when(tokenMapper.from(token, queuePosition)).thenReturn(expectedTokenDomain);

    // Act
    TokenDomain result = useCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(expectedTokenDomain, result);
    verify(tokenService).upsertToken(reserverId);
    verify(tokenMapper).from(token, queuePosition);
  }

  @Test
  void execute_ShouldThrowException_WhenTokenServiceFails() {
    Long reserverId = 1L;
    IssueTokenUseCommand command = IssueTokenUseCommand.builder()
            .reserverId(reserverId)
                .build();
    when(tokenService.upsertToken(reserverId)).thenThrow(new RuntimeException("Service failed"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> useCase.execute(command));
    verify(tokenService).upsertToken(reserverId);
    verifyNoInteractions(tokenMapper);
  }
}