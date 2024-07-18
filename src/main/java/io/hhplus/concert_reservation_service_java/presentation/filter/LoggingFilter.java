package io.hhplus.concert_reservation_service_java.presentation.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.dto.ErrorResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
    ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
    long startTime = System.currentTimeMillis();
    logRequest(httpServletRequest);
    filterChain.doFilter(httpServletRequest, httpServletResponse);
    long endTime = System.currentTimeMillis();
    log.info("INFO :: time: {}ms", endTime - startTime);
    logResponse(httpServletResponse);
    httpServletResponse.copyBodyToResponse();
  }

  private void logRequest (ContentCachingRequestWrapper request){
    log.info("Request :: URL: {}", request.getRequestURI());
    log.info("Request :: HTTP Method: {}", request.getMethod());
    log.info("Request :: Token : {}", request.getHeader("Authorization"));
  }
  private void logResponse (ContentCachingResponseWrapper response){
    String responseContent = new String(response.getContentAsByteArray());
    log.info("Response :: Status: {}", response.getStatus());
    log.info("Response :: ResponseContent: {}", responseContent);
  }
}
