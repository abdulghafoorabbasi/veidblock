package org.acreo.auth.util;

import org.acreo.ip.service.VerificationService;

import com.codahale.metrics.health.HealthCheck;

public class VerificationHealthCheck extends HealthCheck {
  private static final String HEALTHY = "The Verification Service is healthy for read and write";
  private static final String UNHEALTHY = "The Verification Service is not healthy. ";
  private static final String MESSAGE_PLACEHOLDER = "{}";

  private final VerificationService verificationService;

  public VerificationHealthCheck(VerificationService verificationService) {
    this.verificationService = verificationService;
  }

  @Override
  public Result check() throws Exception {
    String mySqlHealthStatus = verificationService.performHealthCheck();

    if (mySqlHealthStatus == null) {
      return Result.healthy(HEALTHY);
    } else {
      return Result.unhealthy(UNHEALTHY + MESSAGE_PLACEHOLDER, mySqlHealthStatus);
    }
  }
}
