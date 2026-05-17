package com.pulsefit.domain;

import com.pulsefit.domain.DomainEnums.PaymentStatus;
import com.pulsefit.domain.DomainEnums.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "payments")
public class Payment {

  @Id
  @UuidGenerator
  public UUID id;

  @OneToOne
  @JoinColumn(name = "booking_id")
  public Booking booking;

  @ManyToOne
  @JoinColumn(name = "subscription_id")
  public Subscription subscription;

  @Column(name = "stripe_checkout_session_id", unique = true)
  public String stripeCheckoutSessionId;

  @Column(name = "stripe_payment_intent_id")
  public String stripePaymentIntentId;

  @Column(name = "stripe_invoice_id")
  public String stripeInvoiceId;

  @Column(name = "amount_eur", nullable = false)
  public BigDecimal amountEur;

  @Column(name = "commission_eur", nullable = false)
  public BigDecimal commissionEur;

  @Column(nullable = false)
  public String currency = "eur";

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public PaymentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public PaymentType type;

  @Column(name = "idempotency_key")
  public String idempotencyKey;

  @Column(name = "event_id", unique = true)
  public String eventId;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  public Instant updatedAt;
}
