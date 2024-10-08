package io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  @Query("SELECT r FROM User r WHERE r.id = :id")
  Optional<User> findById(@Param("id") long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM User r WHERE r.id = :id")
  @QueryHints({
      @QueryHint(name = "javax.persistence.lock.timeout", value = "7000")})
  Optional<User> findByIdWithPessimisticLock(@Param("id")long id);
}
