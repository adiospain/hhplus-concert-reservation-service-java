package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;

public interface CreatePaymentUseCase {

  PaymentDTO execute(CreatePaymentCommand command);
}
