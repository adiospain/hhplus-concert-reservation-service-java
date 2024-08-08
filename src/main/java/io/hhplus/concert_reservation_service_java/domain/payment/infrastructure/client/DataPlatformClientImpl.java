package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.client;

import io.hhplus.concert_reservation_service_java.domain.common.DataPlatformClient;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import org.springframework.stereotype.Component;

@Component
public class DataPlatformClientImpl implements DataPlatformClient {


  @Override
  public void send(String key, Payment payment) {

  }
}
