package io.hhplus.concert_reservation_service_java.unit.useCase.token;

import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.useCase.TouchExpiredTokenUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.token.TouchExpiredTokenUseCase;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;


import static org.mockito.Mockito.*;

class TouchExpiredTokenUseCaseTest {
  private TokenService tokenService = Mockito.mock(TokenService.class);
  private TouchExpiredTokenUseCase useCase = new TouchExpiredTokenUseCaseImpl(tokenService);

//  @Test
//  void execute_WhenExpiredTokensExist_ShouldActivateNextTokens() {
//
//    LocalDateTime now = LocalDateTime.now();
//    int updatedCount = 2;
//    List<Token> expiredTokens = Arrays.asList(
//        new Token(1L, 5L),
//        new Token(2L, 22L)
//    );
//
//    when(tokenService.bulkUpdateExpiredTokens()).thenReturn(updatedCount);
//    when(tokenService.getExpiredTokens()).thenReturn(expiredTokens);
//
//    // Act
//    useCase.execute();
//
//    // Assert
//    verify(tokenService, times(1)).bulkUpdateExpiredTokens();
//    verify(tokenService, times(1)).getExpiredTokens();
//    verify(tokenService, times(2)).activateNextToken(anyLong(), any(LocalDateTime.class));
//    verify(tokenService).activateNextToken(eq(1L), any(LocalDateTime.class));
//    verify(tokenService).activateNextToken(eq(2L), any(LocalDateTime.class));
//  }
  @Test
  void execute_WhenNoExpiredTokens_ShouldNotActivateNextTokens() {

    when(tokenService.bulkUpdateExpiredTokens()).thenReturn(0);

    useCase.execute();

    verify(tokenService, times(1)).bulkUpdateExpiredTokens();
    verify(tokenService, never()).getExpiredTokens();
    verify(tokenService, never()).activateNextToken(anyLong());
  }
}