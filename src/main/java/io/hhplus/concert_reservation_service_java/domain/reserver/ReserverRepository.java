package io.hhplus.concert_reservation_service_java.domain.reserver;

import java.util.Optional;

public interface ReserverRepository {

  Optional<Reserver> findById(long reserverId);
}
