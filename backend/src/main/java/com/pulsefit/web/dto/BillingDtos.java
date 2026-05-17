package com.pulsefit.web.dto;

public final class BillingDtos {
  private BillingDtos() {}

  public record CheckoutResponse(String checkoutSessionId, String url) {}
  public record PortalResponse(String url) {}
  public record RefundResponse(String paymentId, String status) {}
}
