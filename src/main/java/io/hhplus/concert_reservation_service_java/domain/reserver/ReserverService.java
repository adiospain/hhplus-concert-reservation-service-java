package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;

public interface ReserverService {

  Reserver getReserverWithLock(long reserverId);

  Reserver chargePoint(long reserverId, int amount);

  Reserver save(Reserver reserver);
}
