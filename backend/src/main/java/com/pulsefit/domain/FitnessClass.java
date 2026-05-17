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
@Table(name = "fitness_classes")
public class FitnessClass {

  @Id
  @UuidGenerator
  public UUID id;

  @ManyToOne
  @JoinColumn(name = "studio_id", nullable = false)
  public Studio studio;

  @ManyToOne
  @JoinColumn(name = "instructor_id", nullable = false)
  public Instructor instructor;

  @Column(nullable = false)
  public String title;

  @Column(nullable = false)
  public String discipline;

  @Column(nullable = false, columnDefinition = "text")
  public String description;

  @Column(name = "duration_minutes", nullable = false)
  public int durationMinutes;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;
}
