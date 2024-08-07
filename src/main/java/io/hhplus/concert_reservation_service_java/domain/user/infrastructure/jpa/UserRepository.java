package io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findById(long reserverId);

  User save(User reserver);

  Optional<User> findByIdWithPessimisticLock(long reserverId);

  void saveAll(List<User> users);
}
