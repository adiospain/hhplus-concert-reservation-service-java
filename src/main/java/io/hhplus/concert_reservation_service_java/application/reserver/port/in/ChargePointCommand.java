package io.hhplus.concert_reservation_service_java.application.reserver.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChargePointCommand {
  long userId;
  int amount;
}
