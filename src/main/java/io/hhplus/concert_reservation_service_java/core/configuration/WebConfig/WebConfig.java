package io.hhplus.concert_reservation_service_java.core.configuration.WebConfig;

import io.hhplus.concert_reservation_service_java.presentation.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
@Component
public class WebConfig implements WebMvcConfigurer {

  private final TokenInterceptor tokenInterceptor;


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tokenInterceptor)
        .addPathPatterns("/api/concerts/**", "/api/users/**", "/api/reservations/**");
  }
}