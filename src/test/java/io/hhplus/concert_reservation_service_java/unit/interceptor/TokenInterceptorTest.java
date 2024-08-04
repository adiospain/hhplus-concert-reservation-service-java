package io.hhplus.concert_reservation_service_java.unit.interceptor;

import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.interceptor.TokenInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenInterceptorTest {
  @Mock
  private TokenService tokenService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private TokenInterceptor tokenInterceptor;

  @BeforeEach
  void setUp() {

  }

  @Test
  @DisplayName("토큰 발급 요청 시 통과")
  void preHandle_TokenIssuanceRequest_ShouldPass() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/users/123/token");
    when(request.getMethod()).thenReturn("POST");

    boolean result = tokenInterceptor.preHandle(request, response, null);

    assertTrue(result);
  }

  @Test
  @DisplayName("토큰 발급 외 다른 요청 시 토큰 없으면 예외 발생")
  void testPreHandle_NoToken_ShouldThrowException() {
    when(request.getRequestURI()).thenReturn("/api/users/12/token");
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader("Authorization")).thenReturn(null);

    assertThrows(CustomException.class, () -> tokenInterceptor.preHandle(request, response, null));
  }

  @Test
  @DisplayName("유효한 토큰으로 요청 시 통과")
  void testPreHandle_ValidToken_ShouldPass() {

    Token validToken = Token.createWaitingToken(12L);
    TokenDomain tokenDomain = new TokenDomain(validToken);

    when(request.getRequestURI()).thenReturn("/api/users/12/token");
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader("Authorization")).thenReturn("validToken");
    when(tokenService.getToken("validToken")).thenReturn(tokenDomain);

    boolean result = tokenInterceptor.preHandle(request, response, null);

    assertTrue(result);
  }

  @Test
  @DisplayName("유효하지 않은 토큰으로 요청 시 예외 발생")
  void testPreHandle_InvalidToken_ShouldThrowException() {
    when(request.getRequestURI()).thenReturn("/api/users/12/token");
    when(request.getMethod()).thenReturn("GET");
    when(request.getHeader("Authorization")).thenReturn("invalidToken");
    when(tokenService.getToken("invalidToken")).thenThrow(new CustomException(ErrorCode.NOT_YET));

    assertThrows(CustomException.class, () -> tokenInterceptor.preHandle(request, response, null));
  }
}
