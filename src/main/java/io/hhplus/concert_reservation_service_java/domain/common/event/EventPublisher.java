package io.hhplus.concert_reservation_service_java.domain.common.event;

public interface EventPublisher {
  void execute(CustomEvent event);
}
