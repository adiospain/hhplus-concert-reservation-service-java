package io.hhplus.concert_reservation_service_java.domain.common.message;

import org.springframework.beans.factory.annotation.Value;

public interface MessageSender {
  void send(String message);
}
