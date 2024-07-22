package io.hhplus.concert_reservation_service_java.domain.user.business.service;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  private static final int MAX_RETRIES = 3;
  private static final long RETRY_DELAY = 100;

  @Override
  public User getUser(long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public User getUserWithLock(long userId) {
    return userRepository.findByIdWithPessimisticLock(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
  public int getPoint(long userId) {
    User user = this.getUserWithLock(userId);
    return user.getPoint();
  }

  @Override
  @Transactional (isolation = Isolation.READ_COMMITTED)
  public User chargePoint(long userId, int amount) {
//    User user = this.getUserWithLock(userId);
//    user.chargePoint(amount);
//    User savedUser = userRepository.save(user);
//    return savedUser;

    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
      try {
        User user = this.getUserWithLock(userId);
        user.chargePoint(amount);
        return userRepository.save(user);
      } catch (ObjectOptimisticLockingFailureException e) {
        if (attempt == MAX_RETRIES - 1) {
          throw new CustomException(ErrorCode.CONCURRENT_LOCK);
        }
        try {
          Thread.sleep(RETRY_DELAY);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new CustomException(ErrorCode.OPERATION_INTERRUPTED);
        }
      }
    }
    throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
  }

  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public User usePoint(long userId, Integer price) {
    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
      try {
        User user = this.getUserWithLock(userId);
        user.usePoint(price);
        return userRepository.save(user);
      } catch (ObjectOptimisticLockingFailureException e) {
        if (attempt == MAX_RETRIES - 1) {
          throw new CustomException(ErrorCode.CONCURRENT_LOCK);
        }
        try {
          Thread.sleep(RETRY_DELAY);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new CustomException(ErrorCode.OPERATION_INTERRUPTED);
        }
      }
    }
    throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }


}
