package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.CreatePaymentCommand;

public interface CreatePaymentUseCase {

  PaymentDomain execute(CreatePaymentCommand command);
}
