package io.hhplus.concert_reservation_service_java.application.token.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueTokenUseCommand {
  long userId;
}
