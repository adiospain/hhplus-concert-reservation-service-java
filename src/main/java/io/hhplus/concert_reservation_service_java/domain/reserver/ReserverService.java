package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;

public interface ReserverService {

  Reserver getReserver(long reserverId);
  Reserver getReserverWithLock(long reserverId);

  int getPoint(long reserverId);
  Reserver chargePoint(long reserverId, int amount);

  Reserver save(Reserver reserver);

  Reserver usePoint(long reserverId, Integer reservedPrice);
}
