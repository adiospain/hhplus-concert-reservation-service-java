package io.hhplus.concert_reservation_service_java.domain.common;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;

  public interface DataPlatformClient {

    boolean send(String key, Payment payment);
  }
