package io.hhplus.concert_reservation_service_java.domain.user.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userRepository;


  @Override
  public Optional<User> findById(long reserverId) {
    return userRepository.findById(reserverId);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Optional<User> findByIdWithPessimisticLock(long reserverId) {
    return userRepository.findByIdWithPessimisticLock(reserverId);
  }



  @Override
  public User save(User reserver) {
    return userRepository.save(reserver);
  }

  @Override
  public void saveAll(List<User> users) {
    userRepository.saveAll(users);
  }
}
