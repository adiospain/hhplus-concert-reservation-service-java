package io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetPointCommand {
  long reserverId;
}
