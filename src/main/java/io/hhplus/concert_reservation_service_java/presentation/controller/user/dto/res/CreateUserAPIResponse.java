package io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res;

public record CreateUserAPIResponse (long userId) {

  public static CreateUserAPIResponse from(long userId) {
    return new CreateUserAPIResponse(userId);
  }
}
