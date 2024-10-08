package io.hhplus.concert_reservation_service_java.exception.dto;

import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
  private final LocalDateTime timestamp = LocalDateTime.now();
  private final int statusCode;
  private final String statusCodeName;
  private final String code;
  private final String message;
  private final String runtimeValue;

  public static ResponseEntity<ErrorResponse> toResponseEntity(
      ErrorCode errorCode, String runtimeValue
  ) {
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(ErrorResponse.builder()
            .statusCode(errorCode.getHttpStatus().value())
            .statusCodeName(errorCode.getHttpStatus().name())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .runtimeValue(runtimeValue)
            .build()
        );
  }

  public static ErrorResponse of(ErrorCode errorCode, String runtimeValue) {
    return ErrorResponse.builder()
        .statusCode(errorCode.getHttpStatus().value())
        .statusCodeName(errorCode.getHttpStatus().name())
        .code(errorCode.name())
        .message(errorCode.getMessage())
        .runtimeValue(runtimeValue)
        .build();
  }
}