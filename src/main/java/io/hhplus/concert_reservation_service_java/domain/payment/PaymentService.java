package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;

public interface PaymentService {

  Payment save(Payment payment);

  Payment createPayment(long reserverId, long reservationId);
}
