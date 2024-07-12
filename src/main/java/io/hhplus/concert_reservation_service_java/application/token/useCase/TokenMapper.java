  package io.hhplus.concert_reservation_service_java.application.token.useCase;

  import io.hhplus.concert_reservation_service_java.domain.token.Token;
  import io.hhplus.concert_reservation_service_java.exception.CustomException;
  import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
  import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
  import org.springframework.stereotype.Component;

    @Component
    public class TokenMapper {


      public TokenDTO from(Token token, long order){
        if (token == null){
          throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
        }
        return TokenDTO.builder()
            .id(token.getId())
            .expiredAt(token.getExpireAt())
            .order(order)
            .build();
      }
    }
