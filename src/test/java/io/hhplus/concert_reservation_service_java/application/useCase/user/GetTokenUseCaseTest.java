package io.hhplus.concert_reservation_service_java.application.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenUseCommand;
import io.hhplus.concert_reservation_service_java.domain.user.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.application.useCase.GetTokenUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTokenUseCaseTest {


  private TokenService tokenService = Mockito.mock(TokenService.class);
  private GetTokenUseCase useCase = new GetTokenUseCaseImpl(tokenService);

  @Test
  void execute_ShouldReturnTokenDTO_WhenTokenExists() {
    // Arrange
    Long reserverId = 1L;
    GetTokenUseCommand command = GetTokenUseCommand.builder()
        .reserverId(reserverId)
        .build();
    Token token = new Token();
    int queuePosition = 5;
    TokenDomain expectedTokenDomain = new TokenDomain();

    when(tokenService.getToken(reserverId, token.getAccessKey())).thenReturn(expectedTokenDomain);

    TokenDomain result = useCase.execute(command);

    assertNotNull(result);
    assertEquals(expectedTokenDomain, result);
    verify(tokenService).getToken(reserverId, token.getAccessKey());
  }

//  @Test
//  void execute_ShouldIssueNewToken_WhenTokenNotFound() {
//    // Arrange
//    Long reserverId = 1L;
//    GetTokenUseCommand command = GetTokenUseCommand.builder()
//        .reserverId(reserverId)
//        .build();
//    Token token = new Token(999L, 1L);
//    when(tokenService.getToken(reserverId)).thenReturn(token);
//
//    TokenDTO result = useCase.execute(command);
//
//    assertNotNull(result);
//    verify(tokenService).getToken(reserverId);
//    verify(tokenMapper).from(token, any());
//  }
}