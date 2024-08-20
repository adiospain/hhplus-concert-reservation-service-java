package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.CreateUserUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateUserCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CreateUserUseCaseImpl implements CreateUserUseCase {

  private final UserService userService;

  @Override
  public long execute(CreateUserCommand command) {
    User user = User.builder()
        .id(command.getUserId())
        .point(0)
        .build();
    User savedUser = userService.save(user);
    return savedUser.getId();
  }
}
