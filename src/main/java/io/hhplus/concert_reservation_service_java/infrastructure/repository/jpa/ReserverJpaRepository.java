package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ReserverJpaRepository extends JpaRepository<Reserver, Long> {

  Optional<Reserver> findById(long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM Reserver r WHERE r.id = :reserverId")
  Optional<Reserver> findByIdWithPessimisticLock(long reserverId);
}
