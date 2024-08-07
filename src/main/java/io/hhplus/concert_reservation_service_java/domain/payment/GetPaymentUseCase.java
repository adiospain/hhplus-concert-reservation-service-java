package io.hhplus.concert_reservation_service_java.domain.payment;

import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.port.in.GetPaymentCommand;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface GetPaymentUseCase {


  List<PaymentDomain> execute(GetPaymentCommand command);
}
