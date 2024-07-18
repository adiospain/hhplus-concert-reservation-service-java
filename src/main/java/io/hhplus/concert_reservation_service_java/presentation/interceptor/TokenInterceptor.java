package io.hhplus.concert_reservation_service_java.presentation.interceptor;

import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenInterceptor implements HandlerInterceptor {
  private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) throws CustomException {
      log.info("INFO :: TokenInterceptor");
      String requestURI = request.getRequestURI();

      String accessKey = request.getHeader("Authorization");
      if (StringUtils.isEmpty(accessKey)){
        throw new CustomException(ErrorCode.NO_TOKEN);
      }
      try {
        Token token = tokenService.getTokenByAccessKey(accessKey);
        return true;
      } catch (CustomException e){
        log.error("ERROR :: Token validation failed for request to {}: {}", requestURI, e.getMessage());
          throw new CustomException(ErrorCode.NOT_YET);
      }
    }
}
