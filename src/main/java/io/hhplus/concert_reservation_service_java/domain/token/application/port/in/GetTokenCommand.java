package io.hhplus.concert_reservation_service_java.domain.token.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTokenCommand {
  String accessKey;
  long userId;
}
