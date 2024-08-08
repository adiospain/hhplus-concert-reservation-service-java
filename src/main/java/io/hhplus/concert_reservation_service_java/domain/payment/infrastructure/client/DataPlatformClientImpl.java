package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.client;

import io.hhplus.concert_reservation_service_java.domain.common.DataPlatformClient;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataPlatformClientImpl implements DataPlatformClient {


  @Override
  public boolean send(String key, Payment payment) {
    log.info("key: {}::성공적으로 예약 정보를 전송했습니다. 결제 id: {}",key payment);
    return true;
  }
}
