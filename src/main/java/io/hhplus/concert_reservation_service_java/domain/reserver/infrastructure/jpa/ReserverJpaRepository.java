package io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReserverJpaRepository extends JpaRepository<Reserver, Long> {

  @Query("SELECT r FROM Reserver r WHERE r.id = :id")
  Optional<Reserver> findById(@Param("id") long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM Reserver r WHERE r.id = :reserverId")
  Optional<Reserver> findByIdWithPessimisticLock(@Param("reserverId")long reserverId);
}
