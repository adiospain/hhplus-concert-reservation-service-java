package io.hhplus.concert_reservation_service_java.unit.service;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.business.service.TokenServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

  @Mock
  private TokenRepository tokenRepository;

  private TokenServiceImpl tokenService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    tokenService = new TokenServiceImpl(tokenRepository);
  }

  @Test
  void upsertToken_WhenTokenExists_ShouldUpdateToken() {
    // Given
    long UserId = 1L;
    String accessKey = "existingAccessKey";
    Token token = Token.builder()
        .id(23L)
        .userId(userId)
        .accessKey(UUID.randomUUID().toString())
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();
    when(tokenRepository.findByUserId(userId)).thenReturn(Optional.of(token));
    when(tokenRepository.save(any(Token.class))).thenReturn(token);
    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.of(1L));

    // When
    TokenDomain result = tokenService.upsertToken(UserId, accessKey);

    // Then
    assertNotNull(result);
    verify(tokenRepository).save(any(Token.class));
  }

  @Test
  void upsertToken_WhenTokenDoesNotExist_ShouldCreateNewToken() {
    // Given
    long UserId = 1L;
    String accessKey = "newAccessKey";
    when(tokenRepository.findByUserId(UserId)).thenReturn(Optional.empty());
    when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.empty());

    // When
    TokenDomain result = tokenService.upsertToken(UserId, accessKey);

    // Then
    assertNotNull(result);
    verify(tokenRepository).save(any(Token.class));
  }

  @Test
  void getToken_WhenTokenExists_ShouldReturnToken() {
    // Given
    long UserId = 1L;
    String accessKey = "existingAccessKey";
    Token existingToken = Token.createWaitingToken(UserId);
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.of(existingToken));
    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.of(1L));

    // When
    TokenDomain result = tokenService.getToken(UserId, accessKey);

    // Then
    assertNotNull(result);
  }

  @Test
  void getToken_WhenTokenDoesNotExist_ShouldThrowException() {
    // Given
    long UserId = 1L;
    String accessKey = "nonExistingAccessKey";
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(CustomException.class, () -> tokenService.getToken(UserId, accessKey));
  }

  @Test
  void bulkUpdateExpiredTokens_ShouldReturnUpdatedCount() {
    // Given
    when(tokenRepository.bulkUpdateExpiredTokens(any(LocalDateTime.class))).thenReturn(5);

    // When
    int result = tokenService.bulkUpdateExpiredTokens();

    // Then
    assertEquals(5, result);
    verify(tokenRepository).bulkUpdateExpiredTokens(any(LocalDateTime.class));
  }

  @Test
  void getExpiredTokens_ShouldReturnListOfExpiredTokens() {
    // Given
    Token expiredToken1 = new Token();
    Token expiredToken2 = new Token();
    when(tokenRepository.findExpiredTokens(any(LocalDateTime.class))).thenReturn(Arrays.asList(expiredToken1, expiredToken2));

    // When
    List<Token> result = tokenService.getExpiredTokens();

    // Then
    assertEquals(2, result.size());
    verify(tokenRepository).findExpiredTokens(any(LocalDateTime.class));
  }

  @Test
  void setTokenStatusToDone_ShouldCallRepositoryMethod() {
    // Given
    long tokenId = 1L;

    // When
    tokenService.setTokenStatusToDone(tokenId);

    // Then
    verify(tokenRepository).setTokenStatusToDone(tokenId);
  }

  @Test
  void activateNextToken_ShouldReturnActivatedCount() {
    // Given
    long tokenId = 1L;
    when(tokenRepository.activateNextToken(eq(tokenId), any(LocalDateTime.class))).thenReturn(1);

    // When
    int result = tokenService.activateNextToken(tokenId);

    // Then
    assertEquals(1, result);
    verify(tokenRepository).activateNextToken(eq(tokenId), any(LocalDateTime.class));
  }

  @Test
  void getTokenByAccessKey_WhenTokenExists_ShouldReturnToken() {
    // Given
    String accessKey = "existingAccessKey";
    long UserId = 2L;
    Token existingToken = Token.createWaitingToken(UserId);
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.of(existingToken));

    // When
    Token result = tokenService.getTokenByAccessKey(accessKey);

    // Then
    assertNotNull(result);
    assertEquals(accessKey, result.getAccessKey());
  }

  @Test
  void getTokenByAccessKey_WhenTokenDoesNotExist_ShouldThrowException() {
    // Given
    String accessKey = "nonExistingAccessKey";
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(CustomException.class, () -> tokenService.getTokenByAccessKey(accessKey));
  }

  @Test
  void completeTokenAndActivateNextToken_ShouldCallBothMethods() {
    // Given
    long tokenId = 1L;
    doNothing().when(tokenRepository).setTokenStatusToDone(tokenId);
    when(tokenRepository.activateNextToken(eq(tokenId), any(LocalDateTime.class))).thenReturn(1);

    // When
    tokenService.completeTokenAndActivateNextToken(tokenId);

    // Then
    verify(tokenRepository).setTokenStatusToDone(tokenId);
    verify(tokenRepository).activateNextToken(eq(tokenId), any(LocalDateTime.class));
  }
}