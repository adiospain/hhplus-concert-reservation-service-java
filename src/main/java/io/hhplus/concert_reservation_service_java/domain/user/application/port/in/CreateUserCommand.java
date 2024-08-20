package io.hhplus.concert_reservation_service_java.domain.user.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateUserCommand {
  long userId;
}
