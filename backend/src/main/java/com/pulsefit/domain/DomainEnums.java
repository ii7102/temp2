package com.pulsefit.domain;

public final class DomainEnums {
  private DomainEnums() {}

  public enum RoleName { GUEST, USER, ADMIN }
  public enum BookingStatus { PENDING_PAYMENT, CONFIRMED, COMPLETED, CANCELLED, REFUNDED }
  public enum PaymentStatus { PENDING, SUCCEEDED, FAILED, REFUNDED }
  public enum PaymentType { SESSION, SUBSCRIPTION, REFUND }
  public enum SubscriptionStatus { PENDING, ACTIVE, CANCELED, PAST_DUE }
  public enum SessionStatus { PUBLISHED, CANCELLED }
}
