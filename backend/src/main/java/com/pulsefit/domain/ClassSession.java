package com.pulsefit.domain;

import com.pulsefit.domain.DomainEnums.SessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "class_sessions")
public class ClassSession {

  @Id
  @UuidGenerator
  public UUID id;

  @ManyToOne
  @JoinColumn(name = "class_id", nullable = false)
  public FitnessClass fitnessClass;

  @Column(name = "starts_at", nullable = false)
  public Instant startsAt;

  @Column(name = "ends_at", nullable = false)
  public Instant endsAt;

  @Column(nullable = false)
  public int capacity;

  @Column(name = "booked_count", nullable = false)
  public int bookedCount;

  @Column(name = "location_name", nullable = false)
  public String locationName;

  @Column(name = "price_eur", nullable = false)
  public BigDecimal priceEur;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public SessionStatus status = SessionStatus.PUBLISHED;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;
}
