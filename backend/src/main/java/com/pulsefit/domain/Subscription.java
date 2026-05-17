package com.pulsefit.domain;

import com.pulsefit.domain.DomainEnums.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "subscriptions")
public class Subscription {

  @Id
  @UuidGenerator
  public UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  public AppUser user;

  @Column(name = "stripe_subscription_id", unique = true)
  public String stripeSubscriptionId;

  @Column(name = "stripe_customer_id")
  public String stripeCustomerId;

  @Column(name = "price_id")
  public String priceId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public SubscriptionStatus status = SubscriptionStatus.PENDING;

  @Column(name = "credits_balance", nullable = false)
  public int creditsBalance;

  @Column(name = "credits_reset_at")
  public Instant creditsResetAt;

  @Column(name = "current_period_end")
  public Instant currentPeriodEnd;

  @Column(name = "cancelled_at")
  public Instant cancelledAt;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  public Instant updatedAt;
}
