package io.hhplus.concert_reservation_service_java.presentation.filter;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

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
      String requestURI = httpServletRequest.getRequestURI();

      if (shouldSkip(requestURI)){
        filterChain.doFilter(servletRequest, servletResponse);
        return;
      }

      long startTime = System.currentTimeMillis();

      filterChain.doFilter(httpServletRequest, httpServletResponse);

      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;
      double durationMillis = duration / 1_000_000.0;

      logRequestAndResponse(httpServletRequest, httpServletResponse, durationMillis);
      httpServletResponse.copyBodyToResponse();
    }

  private boolean shouldSkip(String requestURI) {
    if (requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v2/api-docs") || requestURI.startsWith("/swagger-resources") || requestURI.startsWith("/v3/api-docs")) {
      return true;
    }
    return false;
  }

  private void logRequestAndResponse (ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, double duration){
      String requestBody = new String(request.getContentAsByteArray());
      String responseBody = new String(response.getContentAsByteArray());

      log.info("API Call - URL: {}, Method: {}, Status: {}, Duration: {} ms, Request: {}, Response: {}",
          request.getRequestURI(),
          request.getMethod(),
          response.getStatus(),
          duration,
          requestBody,
          responseBody);
    }
}
