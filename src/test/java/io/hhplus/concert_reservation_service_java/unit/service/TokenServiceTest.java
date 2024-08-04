package io.hhplus.concert_reservation_service_java.service;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.token.business.service.TokenServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.TokenStatus;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.TokenRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  @DisplayName("upsertToken::이미 존재하는 토큰 있으면 토큰 업데이트")
  void upsertToken_WhenTokenExists_ShouldUpdateToken() {
    // Given
    long reserverId = 1L;
    String accessKey = "existingAccessKey";

    Token existingToken = Token.builder()
        .id(23L)
        .userId(reserverId)
        .accessKey(UUID.randomUUID().toString())
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();

    when(tokenRepository.findByUserIdAndAccessKey(reserverId, accessKey)).thenReturn(Optional.of(existingToken));
    when(tokenRepository.save(any(Token.class))).thenReturn(existingToken);
    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.of(1L));

    // When
    TokenDomain result = tokenService.upsertToken(reserverId, accessKey);

    // Then
    assertNotNull(result);
    verify(tokenRepository).save(any(Token.class));
    verify(tokenRepository, times(1)).save(existingToken);
    assertEquals(0, result.getQueuePosition());
  }

  @Test
  @DisplayName("upsertToken::이미 존재하는 토큰 없으면 새로운 토큰 생성")
  void upsertToken_WhenTokenDoesNotExist_ShouldCreateNewToken() {
    // Given
    long reserverId = 1L;
    String accessKey = "newAccessKey";

    Token token = Token.builder()
        .id(2L)
        .userId(reserverId)
        .accessKey(UUID.randomUUID().toString())
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();

    tokenRepository.deleteAll();

    when(tokenRepository.findByUserIdAndAccessKey(reserverId, accessKey)).thenReturn(Optional.empty());
    when(tokenRepository.save(any(Token.class))).thenReturn(token);
    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.empty());

    // When
    TokenDomain result = tokenService.upsertToken(reserverId, accessKey);

    // Then
    assertNotNull(result);
    verify(tokenRepository, times(1)).save(any(Token.class));
    assertEquals(0L, result.getQueuePosition());
  }

  @Test
  @DisplayName("upsertToken::헤더에 토큰 없으면 새로운 토큰 생성")
  void upsertToken_WithEmptyAccessKey() {
    when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TokenDomain result = tokenService.upsertToken(2L, "");

    assertNotNull(result);
    assertEquals(2L, result.getUserId());
    verify(tokenRepository, times(1)).save(any(Token.class));
    assertEquals(0L, result.getQueuePosition());
  }

//  @Test
//  @DisplayName("upsertToken::Turn Active 확인")
//  void upsertToken_TurnActiveCall() {
//    // Given
//    long reserverId = 1L;
//    long tokenId = 100L;
//    String accessKey = "newAccessKey";
//
//    Token existingToken = Mockito.mock(Token.class);
//
//    when(tokenRepository.findByUserId(reserverId)).thenReturn(Optional.of(existingToken));
//    when(existingToken.getStatus()).thenReturn(TokenStatus.WAIT);
//    when(existingToken.getId()).thenReturn(tokenId);
//    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.of(tokenId));
//    when(tokenRepository.save(any(Token.class))).thenReturn(existingToken);
//
//    tokenService.upsertToken(reserverId, accessKey);
//
//
//    verify(existingToken, times(1)).turnActive();
//    verify(tokenRepository, times(2)).save(existingToken);
//  }

  @Test
  @DisplayName("getToken::이미 존재하는 토큰 있으면 그 토큰 반환")
  void getToken_WhenTokenExists_ShouldReturnToken() {
    // Given
    long reserverId = 1L;
    String accessKey = UUID.randomUUID().toString();
    Token existingToken = Token.builder()
        .id(23L)
        .userId(reserverId)
        .accessKey(accessKey)
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();
    when(tokenRepository.findByUserIdAndAccessKey(reserverId, accessKey)).thenReturn(Optional.of(existingToken));
    when(tokenRepository.findSmallestActiveTokenId()).thenReturn(Optional.of(1L));

    // When
    TokenDomain result = tokenService.getToken(reserverId, accessKey);

    // Then
    assertNotNull(result);
    assertEquals(0, result.getQueuePosition());
  }

  @Test
  @DisplayName("getToken::이미 존재하는 토큰 없으면 예외처리")
  void getToken_WhenTokenDoesNotExist_ShouldThrowException() {
    // Given
    long reserverId = 1L;
    String accessKey = "nonExistingAccessKey";
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(CustomException.class, () -> tokenService.getToken(reserverId, accessKey));
  }
  

//  @Test
//  void setTokenStatusToDone_ShouldCallRepositoryMethod() {
//    // Given
//    long tokenId = 1L;
//
//    // When
//    tokenService.setTokenStatusToDone(tokenId);
//
//    // Then
//    verify(tokenRepository).setTokenStatusToDone(tokenId);
//  }


  @Test
  void getTokenByAccessKey_WhenTokenExists_ShouldReturnToken() {
    // Given
    String accessKey = "existingAccessKey";
    long reserverId = 2L;
    Token existingToken = Token.createWaitingToken(reserverId);
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.of(existingToken));

    // When
    TokenDomain result = tokenService.getTokenByAccessKey(accessKey);

    // Then
    assertNotNull(result);
    assertEquals(existingToken.getAccessKey(), result.getAccessKey());
  }

  @Test
  void getTokenByAccessKey_WhenTokenDoesNotExist_ShouldThrowException() {
    // Given
    String accessKey = "nonExistingAccessKey";
    when(tokenRepository.findByAccessKey(accessKey)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(CustomException.class, () -> tokenService.getTokenByAccessKey(accessKey));
  }

//  @Test
//  void completeTokenAndActivateNextToken_ShouldCallBothMethods() {
//    // Given
//    long tokenId = 1L;
//    doNothing().when(tokenRepository).setTokenStatusToDone(tokenId);
//    when(tokenRepository.activateNextToken(eq(tokenId), any(LocalDateTime.class))).thenReturn(1);
//
//    // When
//    tokenService.completeTokenAndActivateNextToken(tokenId);
//
//    // Then
//    verify(tokenRepository).setTokenStatusToDone(tokenId);
//    verify(tokenRepository).activateNextToken(eq(tokenId), any(LocalDateTime.class));
//  }
}