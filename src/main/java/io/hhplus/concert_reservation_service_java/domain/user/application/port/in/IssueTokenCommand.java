package io.hhplus.concert_reservation_service_java.domain.user.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueTokenCommand {
  String accessKey;
  long userId;
}
