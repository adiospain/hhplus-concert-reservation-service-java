package io.hhplus.concert_reservation_service_java.domain.user;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;

public interface UserService {

  User getUser(long userId);
  User getUserWithLock(long userId);

  int getPoint(long userId);
  User chargePoint(long userId, int amount);

  User save(User user);

  User usePoint(long userId, Integer reservedPrice);
}
