package io.hhplus.concert_reservation_service_java.unit.repository;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.redis.TokenRedisRepository;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.redis.TokenRedisRepositoryImpl;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static jodd.util.ThreadUtil.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TokenRedisRepositoryTest {


  @Autowired
  private RedissonClient redissonClient;

  @Autowired
  private TokenRedisRepository tokenRedisRepository;

  private static final String WAIT_QUEUE_KEY = "wait_queue";
  private static final String ACTIVE_QUEUE_KEY = "active_queue";

  @BeforeEach
  void setUp() {
    redissonClient.getKeys().flushall();
  }

  @Test
  void testSave() {
    Token token = Token.builder()
        .userId(2L)
        .accessKey(UUID.randomUUID().toString())
        .build();



    // When
    Token savedToken = tokenRedisRepository.save(token);

    // Then
    RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);

    assertTrue(queue.contains(token.getUserId() + ":" + token.getAccessKey()));
    assertEquals(token, savedToken);
  }

  @Test
  void testGetTokenWhenWaitQueueHasOneToken() {
    // Given
    String accessKey = UUID.randomUUID().toString();
    Token token = Token.builder()
        .userId(2L)
        .accessKey(accessKey)
        .build();

    Token token2 = Token.builder()
        .userId(3L)
        .accessKey(UUID.randomUUID().toString())
        .build();
    tokenRedisRepository.save(token);
    tokenRedisRepository.save(token2);

    // When
    Optional<Token> result = tokenRedisRepository.getToken(token2.getUserId(), token2.getAccessKey());

    // Then
    assertTrue(result.isPresent());
    assertEquals(0, result.get().getPosition());
  }

  @Test
  void testGetTokenWhenActiveQueueHasAvailableSlots() {
    // Given
    Token token = Token.builder()
        .userId(2L)
        .accessKey(UUID.randomUUID().toString())
        .build();
    tokenRedisRepository.save(token);

    // When
    Optional<Token> result = tokenRedisRepository.getToken(1L, "testAccessKey");

    // Then
    assertTrue(result.isPresent());
    assertEquals(0, result.get().getPosition());
  }

  @Test
  void testGetTokenWhenTokenExistsInActiveQueue() {
    // Given
    Token token = Token.builder()
        .userId(2L)
        .accessKey(UUID.randomUUID().toString())
        .build();
    tokenRedisRepository.save(token);
    RScoredSortedSet<String> activeQueue = redissonClient.getScoredSortedSet(ACTIVE_QUEUE_KEY);
    activeQueue.add(System.currentTimeMillis(), "1:testAccessKey");

    // When
    Optional<Token> result = tokenRedisRepository.getToken(1L, "testAccessKey");

    // Then
    assertTrue(result.isPresent());
    assertEquals(1000.0, result.get().getPosition());
  }

  @Test
  void testGetTokenWhenTokenDoesNotExist() {
    // When
    Optional<Token> result = tokenRedisRepository.getToken(1L, "testAccessKey");
    // Then
    assertFalse(result.isPresent());
  }
}