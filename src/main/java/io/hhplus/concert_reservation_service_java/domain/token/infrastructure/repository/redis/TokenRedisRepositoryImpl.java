package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.redis;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
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
  private static final int TOKEN_TTL_MINUTES = 30;

  @Override
  public Token save(Token token) {
    String key = TOKEN_KEY_PREFIX + token.getUserId() + ":" + token.getAccessKey();
    RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    queue.add(System.currentTimeMillis(),key);
    return token;
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
      return Optional.of(Token.create(userId, accessKey, position));
    }
    return Optional.empty();
  }

  private void activateToken(){
    RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
    RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);
    Collection<String> tokensToActive = waitQueue.pollFirst(MAX_ACTIVE_USER);
    for (String token : tokensToActive){
      activeQueue.add(token, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
      waitQueue.remove(token);
    }
  }
}
