package io.hhplus.concert_reservation_service_java.domain.payment.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPaymentCommand {
  long userId;
}
