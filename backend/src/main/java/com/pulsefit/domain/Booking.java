package com.pulsefit.domain;

import com.pulsefit.domain.DomainEnums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "bookings")
public class Booking {

  @Id
  @UuidGenerator
  public UUID id;

  @ManyToOne
  @JoinColumn(name = "session_id", nullable = false)
  public ClassSession session;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  public AppUser user;

  @ManyToOne
  @JoinColumn(name = "subscription_id")
  public Subscription subscription;

  @OneToOne
  @JoinColumn(name = "payment_id")
  public Payment payment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public BookingStatus status;

  @Column(name = "booked_at", nullable = false)
  public Instant bookedAt;

  @Column(name = "cancelled_at")
  public Instant cancelledAt;

  @Column(name = "attended_at")
  public Instant attendedAt;

  @Column(name = "cancellation_reason")
  public String cancellationReason;

  @Column(name = "review_left", nullable = false)
  public boolean reviewLeft;

  @Column(name = "credits_used", nullable = false)
  public int creditsUsed;
}
