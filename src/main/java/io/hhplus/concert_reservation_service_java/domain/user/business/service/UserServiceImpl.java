package io.hhplus.concert_reservation_service_java.domain.user.business.service;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
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
  @Transactional
  public int getPoint(long userId) {
    long startTime = System.nanoTime();
      try {
        User user = this.getUserWithLock(userId);
        return user.getPoint();
      } catch (StaleObjectStateException | PessimisticLockingFailureException e) {
        throw new CustomException(ErrorCode.CONCURRENT_LOCK);
      }finally{
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double durationMillis = durationNanos / 1_000_000.0;
        log.info("getPoint::userId={}, Duration: {} ms",userId, durationMillis);
      }
  }

  @Override
  @Transactional
  public User chargePoint(long userId, int amount) {
    long startTime = System.nanoTime();
      try {
        User user = this.getUserWithLock(userId);
        user.chargePoint(amount);
        User savedUser = userRepository.save(user);

        long endTime = System.nanoTime();

        long totalDurationNanos = endTime - startTime;
        double totalDurationMillis = totalDurationNanos / 1_000_000.0;

        log.info("chargePoint:: successful - userId={}, amount={}, Total Duration: {} ms",
            userId, amount, totalDurationMillis);

        return savedUser;
      } catch (StaleObjectStateException | PessimisticLockingFailureException e) {
        long endTime = System.nanoTime();

        long totalDurationNanos = endTime - startTime;
        double totalDurationMillis = totalDurationNanos / 1_000_000.0;
        log.warn("chargePoint:: failed - UserId: {}, amount: {}, Total Duration: {} ms",
            userId, amount, totalDurationMillis);
        throw new CustomException(ErrorCode.CONCURRENT_LOCK);
      }finally{
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double durationMillis = durationNanos / 1_000_000.0;
        log.info("chargePoint::userId={}, amount={}, Duration: {} ms",userId, amount, durationMillis);
      }
  }

  @Override
  @Transactional
  public User usePoint(long userId, Integer price) {
    long startTime = System.nanoTime();
      try {
        User user = this.getUserWithLock(userId);
        user.usePoint(price);
        User savedUser = userRepository.save(user);

        long endTime = System.nanoTime();

        long totalDurationNanos = endTime - startTime;
        double totalDurationMillis = totalDurationNanos / 1_000_000.0;

        log.info("usePoint:: successful - userId={}, price={}, Total Duration: {} ms",
            userId, price, totalDurationMillis);

        return savedUser;
      } catch (StaleObjectStateException | PessimisticLockingFailureException e ) {
        long endTime = System.nanoTime();

        long totalDurationNanos = endTime - startTime;
        double totalDurationMillis = totalDurationNanos / 1_000_000.0;
        log.warn("usePoint:: failed - UserId: {}, Price: {}, Total Duration: {} ms",
            userId, price, totalDurationMillis);
          throw new CustomException(ErrorCode.CONCURRENT_LOCK);
      }
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }


}
