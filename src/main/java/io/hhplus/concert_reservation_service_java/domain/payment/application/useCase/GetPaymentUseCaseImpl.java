package io.hhplus.concert_reservation_service_java.domain.payment.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.GetPaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.GetPaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.PaymentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class GetPaymentUseCaseImpl implements GetPaymentUseCase {
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;

  @Override
  @Transactional
  public List<PaymentDomain> execute(GetPaymentCommand command) {
    List<Payment> payments = paymentService.getPayments(command.getUserId());
    return paymentMapper.from(payments);
  }
}
