package io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueTokenUseCommand {
  String accessKey;
  long reserverId;

}
