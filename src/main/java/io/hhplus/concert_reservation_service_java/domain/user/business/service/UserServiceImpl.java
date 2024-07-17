package io.hhplus.concert_reservation_service_java.domain.user.business.service;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public User getUser(long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public User getUserWithLock(long userId) {
    return userRepository.findByIdWithPessimisticLock(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));
  }

  @Override
  @Transactional
  public int getPoint(long userId) {
    User user = this.getUserWithLock(userId);
    return user.getPoint();
  }

  @Override
  @Transactional
  public User chargePoint(long userId, int amount) {
    User user = this.getUserWithLock(userId);
    user.chargePoint(amount);
    User savedUser = userRepository.save(user);
    return savedUser;
  }

  @Override
  public User usePoint(long userId, Integer price) {
    User user = this.getUserWithLock(userId);
    user.usePoint(price);
    User savedUser = userRepository.save(user);
    return savedUser;
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }


}
