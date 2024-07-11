package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.application.payment.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;

public interface CreatePaymentUseCase {

  PaymentDTO execute(CreatePaymentCommand command);
}
