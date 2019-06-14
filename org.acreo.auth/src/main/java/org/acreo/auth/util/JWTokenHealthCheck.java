package org.acreo.auth.util;

import org.acreo.ip.service.JWSTokenService;

import com.codahale.metrics.health.HealthCheck;

public class JWTokenHealthCheck extends HealthCheck {
  private static final String HEALTHY = "The JWTOken Service is healthy for read and write";
  private static final String UNHEALTHY = "The JWToken Service is not healthy. ";
  private static final String MESSAGE_PLACEHOLDER = "{}";

  private final JWSTokenService jwsTokenService;

  public JWTokenHealthCheck(JWSTokenService jwsTokenService) {
    this.jwsTokenService = jwsTokenService;
  }

  @Override
  public Result check() throws Exception {
    String mySqlHealthStatus = jwsTokenService.performHealthCheck();

    if (mySqlHealthStatus == null) {
      return Result.healthy(HEALTHY);
    } else {
      return Result.unhealthy(UNHEALTHY + MESSAGE_PLACEHOLDER, mySqlHealthStatus);
    }
  }
}
