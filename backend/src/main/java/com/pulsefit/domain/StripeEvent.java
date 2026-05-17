package com.pulsefit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "stripe_events")
public class StripeEvent {

  @Id
  @UuidGenerator
  public UUID id;

  @Column(name = "stripe_event_id", nullable = false, unique = true)
  public String stripeEventId;

  @Column(name = "event_type", nullable = false)
  public String eventType;

  @Column(nullable = false, columnDefinition = "jsonb")
  public String payload;

  @Column(name = "processed_at", nullable = false)
  public Instant processedAt;
}
