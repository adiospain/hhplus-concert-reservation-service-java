package io.hhplus.concert_reservation_service_java.application.token.useCase;

import io.hhplus.concert_reservation_service_java.application.token.port.in.GetTokenUseCommand;
import io.hhplus.concert_reservation_service_java.application.token.service.TokenWithPosition;
import io.hhplus.concert_reservation_service_java.domain.reserver.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTokenUseCaseTest {


  private TokenService tokenService = Mockito.mock(TokenService.class);
  private TokenMapper tokenMapper = Mockito.mock(TokenMapper.class);
  private GetTokenUseCase useCase = new GetTokenUseCaseImpl(tokenService, tokenMapper);

  @Test
  void execute_ShouldReturnTokenDTO_WhenTokenExists() {
    // Arrange
    Long reserverId = 1L;
    GetTokenUseCommand command = GetTokenUseCommand.builder()
        .reserverId(reserverId)
        .build();
    Token token = new Token();
    int queuePosition = 5;
    TokenWithPosition tokenWithPosition = new TokenWithPosition(token, queuePosition);
    TokenDTO expectedTokenDTO = new TokenDTO();

    when(tokenService.getToken(reserverId)).thenReturn(tokenWithPosition);
    when(tokenMapper.from(token, queuePosition)).thenReturn(expectedTokenDTO);

    TokenDTO result = useCase.execute(command);

    assertNotNull(result);
    assertEquals(expectedTokenDTO, result);
    verify(tokenService).getToken(reserverId);
    verify(tokenMapper).from(token, queuePosition);
  }

  @Test
  void execute_ShouldIssueNewToken_WhenTokenNotFound() {
    // Arrange
    Long reserverId = 1L;
    GetTokenUseCommand command = GetTokenUseCommand.builder()
        .reserverId(reserverId)
        .build();
    Token token = new Token(999L, 1L);
    when(tokenService.getToken(reserverId)).thenReturn(token);

    TokenDTO result = useCase.execute(command);

    assertNotNull(result);
    verify(tokenService).getToken(reserverId);
    verify(tokenMapper).from(token, any());
  }
}