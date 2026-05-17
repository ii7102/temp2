package com.pulsefit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @UuidGenerator
  public UUID id;

  @ManyToOne
  @JoinColumn(name = "booking_id", nullable = false, unique = true)
  public Booking booking;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  public AppUser user;

  @ManyToOne
  @JoinColumn(name = "studio_id", nullable = false)
  public Studio studio;

  @Column(nullable = false)
  public int rating;

  @Column(nullable = false, columnDefinition = "text")
  public String comment;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;
}
