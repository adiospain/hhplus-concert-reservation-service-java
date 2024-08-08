package io.hhplus.concert_reservation_service_java.domain.payment.business.service;

import io.hhplus.concert_reservation_service_java.domain.payment.business.DataPlatformSendService;
import org.springframework.stereotype.Service;

@Service
public class DataPlatformSendServiceImpl implements DataPlatformSendService {

  @Override
  public boolean send(){
    return true;
  }
}
