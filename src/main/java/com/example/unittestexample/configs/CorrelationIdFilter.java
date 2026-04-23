package com.example.unittestexample.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  private static final String MDC_KEY = "correlationId";
  private static final String HEADER_NAME = "X-Correlation-Id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = request.getHeader("X-Correlation-Id");
    if (!StringUtils.hasText(correlationId)) {
      correlationId = UUID.randomUUID().toString();
    }
    MDC.put(MDC_KEY, correlationId);
    response.setHeader(HEADER_NAME, correlationId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
