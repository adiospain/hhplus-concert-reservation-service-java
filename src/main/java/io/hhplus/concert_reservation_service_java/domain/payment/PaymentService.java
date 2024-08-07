package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import java.util.List;

public interface PaymentService {

  Payment save(Payment payment);

  Payment createPayment(long reserverId, long concertScheduleId, long seatId, int price);

  List<Payment> getPayment(long userId);
}
