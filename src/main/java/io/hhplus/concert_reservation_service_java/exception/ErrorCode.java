package io.hhplus.concert_reservation_service_java.exception;


import static org.springframework.http.HttpStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //400
  //401
  INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰 입니다."),
  EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰 입니다."),

  //404
  CONCERT_NOT_FOUND(NOT_FOUND, "콘서트를 찾을 수 없습니다");

  private final HttpStatus httpStatus;
  private final String message;

}