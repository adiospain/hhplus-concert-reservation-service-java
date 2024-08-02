package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.redis;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TokenRedisRepositoryImpl implements TokenRedisRepository {

  private final RedissonClient redissonClient;
  private static final String WAIT_QUEUE_KEY = "wait_queue";
  private static final String ACTIVE_QUEUE_KEY = "active_queue";
  private static final String TOKEN_KEY_PREFIX = "token:";
  private static final int MAX_ACTIVE_USER = 100;
  private static final int TOKEN_TTL_MINUTES = 1;

  @Override
  public Token save(Token token) {
    String key = TOKEN_KEY_PREFIX + token.getUserId() + ":" + token.getAccessKey();
    RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    queue.add(System.currentTimeMillis(),key);
    return Token.create(token.getUserId(), token.getAccessKey(), queue.rank(key)+1);
  }

  @Override
  public Optional<Token> getToken(String accessKey) {
    RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);

    for (long i = 0; i < Long.MAX_VALUE; ++i){
      String key = TOKEN_KEY_PREFIX + i + ":" + accessKey;
      if (waitQueue.contains(key)){
        return Optional.of(Token.create(i, accessKey, waitQueue.rank(key)+1));
      }
      if (activeQueue.contains(key)){
        return Optional.of(Token.create(i, accessKey, 0));
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<Token> getToken(long userId, String accessKey) {
    String key = TOKEN_KEY_PREFIX + userId + ":" + accessKey;
    RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);

    if (activeQueue.contains(key)){
      return Optional.of(Token.create(userId, accessKey, 0));
    }

    Double score = waitQueue.getScore(key);
    if (score != null){
      int position = waitQueue.rank(key).intValue();
      return Optional.of(Token.create(userId, accessKey, position+1));
    }
    return Optional.empty();
  }

  @Override
  public void deleteAll() {

  }

  @Override
  public List<Token> findAll() {
    List <Token> allTokens = new ArrayList<>();
    allTokens.addAll(findWaitingTokens());
    allTokens.addAll(findActiveTokens());
    return allTokens;
  }

  @Override
  public void touchExpiredTokens() {
    RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);
    for (String token : activeQueue){

    }
  }

  @Override
  public void activateTokens(){
    RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);
    Collection<String> tokensToActive = waitQueue.pollFirst(MAX_ACTIVE_USER);
    for (String token : tokensToActive){
      activeQueue.add(token, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
    }
  }



  @Override
  public List<Token> findWaitingTokens() {
    RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    return waitQueue.stream()
        .map(this::parseTokenFromKey)
        .filter(Objects::nonNull)
        .map(token -> Token.create(token.getUserId(), token.getAccessKey(), waitQueue.rank(TOKEN_KEY_PREFIX + token.getUserId() + ":" + token.getAccessKey())+1))
        .collect(Collectors.toList());
  }

  @Override
  public List<Token> findActiveTokens() {
    RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);
    return activeQueue.stream()
        .map(this::parseTokenFromKey)
        .filter(Objects::nonNull)
        .map(token -> Token.create(token.getUserId(), token.getAccessKey(), 0))
        .collect(Collectors.toList());
  }



  private Token parseTokenFromKey(String key) {
    if (!key.startsWith(TOKEN_KEY_PREFIX)) {
      return null;
    }
    // 접두사 제거
    String keyWithoutPrefix = key.substring(TOKEN_KEY_PREFIX.length());

    // userId와 accessKey 분리
    String[] parts = keyWithoutPrefix.split(":", 2);
    if (parts.length != 2) {
      return null; // 키 형식이 올바르지 않음
    }

    try {
      long userId = Long.parseLong(parts[0]);
      String accessKey = parts[1];

      // Token 객체 생성 및 반환
      return Token.builder()
          .userId(userId)
          .accessKey(accessKey)
          .build();
    } catch (NumberFormatException e) {
      // userId를 파싱할 수 없는 경우
      return null;
    }
  }
}
