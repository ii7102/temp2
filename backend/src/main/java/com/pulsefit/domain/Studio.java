package com.pulsefit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "studios")
public class Studio {

  @Id
  @UuidGenerator
  public UUID id;

  @Column(nullable = false)
  public String name;

  @Column(nullable = false, unique = true)
  public String slug;

  @Column(nullable = false)
  public String neighborhood;

  @Column(nullable = false)
  public String discipline;

  @Column(nullable = false, columnDefinition = "text")
  public String description;

  @Column(name = "price_from_eur", nullable = false)
  public BigDecimal priceFromEur;

  @Column(nullable = false)
  public BigDecimal rating;

  @Column(name = "review_count", nullable = false)
  public int reviewCount;

  @Column(nullable = false)
  public boolean approved;

  @Column(nullable = false)
  public boolean featured;

  @Column(name = "owner_name", nullable = false)
  public String ownerName;

  @Column(name = "cancellation_policy_hours", nullable = false)
  public int cancellationPolicyHours;

  @Column(name = "image_url", nullable = false, columnDefinition = "text")
  public String imageUrl;

  @Column(name = "banner_url", nullable = false, columnDefinition = "text")
  public String bannerUrl;

  @Column(name = "earnings_total_eur", nullable = false)
  public BigDecimal earningsTotalEur;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;
}
