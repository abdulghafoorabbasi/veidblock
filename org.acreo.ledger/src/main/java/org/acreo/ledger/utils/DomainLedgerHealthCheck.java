package org.acreo.ledger.utils;

import org.acreo.ledger.service.VeidblockLedgerService;

import com.codahale.metrics.health.HealthCheck;

public class DomainLedgerHealthCheck extends HealthCheck {
  private static final String HEALTHY = "The Domain-Ledger Service is healthy for read and write";
  private static final String UNHEALTHY = "The Domain-Ledger Service is not healthy. ";
  private static final String MESSAGE_PLACEHOLDER = "{}";

  private final VeidblockLedgerService veidblockLedgerService;

  public DomainLedgerHealthCheck(VeidblockLedgerService veidblockLedgerService) {
    this.veidblockLedgerService = veidblockLedgerService;
  }

  @Override
  public Result check() throws Exception {
    String mySqlHealthStatus = veidblockLedgerService.performHealthCheck();

    if (mySqlHealthStatus == null) {
      return Result.healthy(HEALTHY);
    } else {
      return Result.unhealthy(UNHEALTHY + MESSAGE_PLACEHOLDER, mySqlHealthStatus);
    }
  }
}
