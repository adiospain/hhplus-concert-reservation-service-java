package io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa;

import java.util.Optional;

public interface ReserverRepository {

  Optional<Reserver> findById(long reserverId);

  Reserver save(Reserver reserver);

  Optional<Reserver> findByIdWithPessimisticLock(long reserverId);
}
