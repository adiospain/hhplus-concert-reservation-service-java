package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.port.in.CreatePaymentCommand;

public interface CreatePaymentUseCase {

  PaymentDomain execute(CreatePaymentCommand command);

  void usePoint(long id, int price);
}
