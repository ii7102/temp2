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
@Table(name = "instructors")
public class Instructor {

  @Id
  @UuidGenerator
  public UUID id;

  @ManyToOne
  @JoinColumn(name = "studio_id", nullable = false)
  public Studio studio;

  @Column(name = "full_name", nullable = false)
  public String fullName;

  @Column(nullable = false, columnDefinition = "text")
  public String bio;

  @Column(name = "avatar_url", nullable = false, columnDefinition = "text")
  public String avatarUrl;

  @Column(nullable = false)
  public String specialties;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;
}
