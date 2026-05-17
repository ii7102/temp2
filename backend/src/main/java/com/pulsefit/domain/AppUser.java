package com.pulsefit.domain;

import static com.pulsefit.domain.DomainEnums.RoleName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "app_users")
public class AppUser {

  @Id
  @UuidGenerator
  public UUID id;

  @Column(name = "keycloak_id")
  public String keycloakId;

  @Column(nullable = false, unique = true)
  public String email;

  @Column(name = "full_name", nullable = false)
  public String fullName;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_name", nullable = false)
  public RoleName roleName = RoleName.USER;

  public String city;
  public String neighborhood;

  @Column(name = "marketing_opt_in", nullable = false)
  public boolean marketingOptIn;

  @Column(name = "saved_payment_method_brand")
  public String savedPaymentMethodBrand;

  @Column(name = "saved_payment_method_last4")
  public String savedPaymentMethodLast4;

  @Column(name = "stripe_customer_id")
  public String stripeCustomerId;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  public Instant updatedAt;

  @PrePersist
  void prePersist() {
    Instant now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
