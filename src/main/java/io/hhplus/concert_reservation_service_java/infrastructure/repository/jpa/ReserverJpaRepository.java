package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserverJpaRepository extends JpaRepository<Reserver, Long> {

  Optional<Reserver> findById(long id);
}
