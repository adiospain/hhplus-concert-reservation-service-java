package io.hhplus.concert_reservation_service_java.domain.token.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {


  public TokenDomain from(Token token, long order){
    if (token == null){
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }
    return TokenDomain.builder()
        .id(token.getId())
        .accessKey(token.getAccessKey())
        .expiredAt(token.getExpireAt())
        .order(order)
        .build();
  }
}
