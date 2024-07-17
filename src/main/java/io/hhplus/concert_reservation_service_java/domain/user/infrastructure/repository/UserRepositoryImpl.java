package io.hhplus.concert_reservation_service_java.domain.user.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userRepository;


  @Override
  public Optional<User> findById(long reserverId) {
    return userRepository.findById(reserverId);
  }

  @Override
  public Optional<User> findByIdWithPessimisticLock(long reserverId) {
    return userRepository.findByIdWithPessimisticLock(reserverId);
  }

  @Override
  public User save(User reserver) {
    return userRepository.save(reserver);
  }
}
