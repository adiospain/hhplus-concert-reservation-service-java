package io.hhplus.concert_reservation_service_java.domain.user.business.service;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final int MAX_RETRIES = 20;
    private static final int INITIAL_RETRY_DELAY = 100; // 초기 대기 시간 (밀리초)
    private static final double BACKOFF_MULTIPLIER = 2.0; // 대기 시간 증가 배수
    private static final int MAX_RETRY_DELAY = 1000; // 최대 대기 시간 (밀리초)
  
  @Override
    @Transactional
    public User getUser(long userId) {
      return userRepository.findById(userId)
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

  @Override
  public User getUserWithLock(long userId) {
    return userRepository.findByIdWithPessimisticLock(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

    @Override
    public int getPoint(long userId) {
      long startTime = System.nanoTime();
      for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        long attemptStartTime = System.nanoTime();
        try {
          User user = this.getUser(userId);
          int point = user.getPoint();

          long attemptDurationNanos = System.nanoTime() - attemptStartTime;
          double attemptDurationMillis = attemptDurationNanos / 1_000_000.0;

          long durationNanos = System.nanoTime() - startTime;
          double durationMillis = durationNanos / 1_000_000.0;

          log.info("getPoint:: successful - UserId: {}, Point: {}, Attempt: {}, Attempt Duration: {} ms, Total Duration: {} ms",
              userId, point, attempt + 1, attemptDurationMillis, durationMillis);

          return point;
        } catch (ObjectOptimisticLockingFailureException e) {
          long attemptDurationNanos = System.nanoTime() - attemptStartTime;
          double durationMillis = attemptDurationNanos / 1_000_000.0;
          log.warn("getPoint:: failed - UserId: {}, Attempt: {}, Attempt Duration: {} ms",
              userId, attempt + 1, durationMillis);
          handleRetry(attempt, e);
        }
      }
      long totalDurationNanos = System.nanoTime() - startTime;
      double durationMillis = totalDurationNanos / 1_000_000.0;
      log.error("getPoint:: failed after all retries - UserId: {}, Total Duration: {} ms",
          userId, durationMillis);
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }

    @Override
    public User chargePoint(long userId, int amount) {
      long startTime = System.nanoTime();
      for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        long attemptStartTime = System.nanoTime();
        try {
          User user = this.getUser(userId);
          user.chargePoint(amount);
          User savedUser = userRepository.save(user);

          long attemptDurationNanos = System.nanoTime() - attemptStartTime;
          double attemptDurationMillis = attemptDurationNanos / 1_000_000.0;

          long durationNanos = System.nanoTime() - startTime;
          double durationMillis = durationNanos / 1_000_000.0;

          log.info("chargePoint:: successful - UserId: {}, Amount: {}, Attempt: {}, Attempt Duration: {} ms, Total Duration: {} ms",
              userId, amount, attempt + 1, attemptDurationMillis, durationMillis);

          return savedUser;
        } catch (ObjectOptimisticLockingFailureException e) {
          long attemptDurationNanos = System.nanoTime() - attemptStartTime;
          double durationMillis = attemptDurationNanos / 1_000_000.0;
          log.warn("chargePoint:: failed - UserId: {}, Amount: {}, Attempt: {}, Attempt Duration: {} ms",
              userId, amount, attempt + 1, durationMillis);
          handleRetry(attempt, e);
        }
      }
      long totalDurationNanos = System.nanoTime() - startTime;
      double durationMillis = totalDurationNanos / 1_000_000.0;
      log.error("chargePoint:: failed after all retries - UserId: {}, Amount: {}, Total Duration: {} ms",
          userId, amount, durationMillis);
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }

    @Override
    public User usePoint(long userId, Integer price) {
      long startTime = System.nanoTime();
      for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        long attemptStartTime = System.nanoTime();
        try {
          User user = this.getUser(userId);
          user.usePoint(price);
          User savedUser = userRepository.save(user);

          long attemptDurationNanos = System.nanoTime() - attemptStartTime;
          double attemptDurationMillis = attemptDurationNanos / 1_000_000.0;

          long durationNanos = System.nanoTime() - startTime;
          double durationMillis = durationNanos / 1_000_000.0;

          log.info("usePoint:: successful - userId={}, price={}, Attempt Duration: {} ms, Total Duration: {} ms",
              userId, price,attemptDurationMillis, durationMillis);

          return savedUser;
        } catch (ObjectOptimisticLockingFailureException e) {
          long attemptDurationNanos = System.nanoTime() - attemptStartTime;
          double durationMillis = attemptDurationNanos / 1_000_000.0;
          log.warn("usePoint:: failed - UserId: {}, Price: {}, Attempt: {}, Attempt Duration: {} ms",
              userId, price, attempt + 1, durationMillis);
          handleRetry(attempt, e);
        }
      }
      long totalDurationNanos = System.nanoTime() - startTime;
      double durationMillis = totalDurationNanos / 1_000_000.0;
      log.error("usePoint:: failed after all retries - UserId: {}, Price: {}, Total Duration: {} ms",
          userId, price, durationMillis);
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }
  private void handleRetry(int attempt, ObjectOptimisticLockingFailureException e) {
    if (attempt == MAX_RETRIES - 1) {
      throw new CustomException(ErrorCode.CONCURRENT_LOCK);
    }
    try {
      long delay = calculateBackoffDelay(attempt);
      Thread.sleep(delay);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new CustomException(ErrorCode.OPERATION_INTERRUPTED);
    }
  }

  private long calculateBackoffDelay(int attempt) {
    long delay = (long) (INITIAL_RETRY_DELAY * Math.pow(BACKOFF_MULTIPLIER, attempt));
    return Math.min(delay, MAX_RETRY_DELAY);
  }

}
