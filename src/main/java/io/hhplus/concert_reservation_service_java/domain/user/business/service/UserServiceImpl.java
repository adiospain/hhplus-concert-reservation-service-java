package io.hhplus.concert_reservation_service_java.domain.user.business.service;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
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
      User user = this.getUser(userId);
      return user.getPoint();
    } catch (StaleObjectStateException | ObjectOptimisticLockingFailureException e) {
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
      User user = this.getUser(userId);
      user.chargePoint(amount);
      return userRepository.save(user);
    } catch (StaleObjectStateException | ObjectOptimisticLockingFailureException e) {
      log.error(ErrorCode.CONCURRENT_LOCK.getMessage());
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
      User user = this.getUser(userId);
      user.usePoint(price);
      return userRepository.save(user);
    } catch (StaleObjectStateException | ObjectOptimisticLockingFailureException e ) {
      log.error(ErrorCode.CONCURRENT_LOCK.getMessage());
      throw new CustomException(ErrorCode.CONCURRENT_LOCK);
    }finally{
      long endTime = System.nanoTime();
      long durationNanos = endTime - startTime;
      double durationMillis = durationNanos / 1_000_000.0;
      log.info("usePoint::userId={}, price={}, Duration: {} ms",userId, price, durationMillis);
    }
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }


}
