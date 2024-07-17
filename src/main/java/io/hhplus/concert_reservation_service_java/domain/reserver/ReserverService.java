package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;

public interface ReserverService {

  Reserver getReserverWithLock(long reserverId);

  int getPoint(long reserverId);
  Reserver chargePoint(long reserverId, int amount);

  Reserver save(Reserver reserver);
}
