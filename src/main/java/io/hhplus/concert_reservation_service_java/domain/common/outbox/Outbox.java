package io.hhplus.concert_reservation_service_java.domain.common.outbox;

public interface Outbox {
  String getId();
  String getMessage();
  boolean isCompleted();
}
