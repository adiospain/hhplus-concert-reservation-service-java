package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import org.springframework.transaction.annotation.Transactional;

public interface ReserverService {

  Reserver getReserverWithLock(long reserverId);

  Reserver chargePoint(long reserverId, int amount);
}
