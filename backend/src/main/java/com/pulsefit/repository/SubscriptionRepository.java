package com.pulsefit.repository;

import com.pulsefit.domain.AppUser;
import com.pulsefit.domain.Subscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
  Optional<Subscription> findByUserAndStatusIn(AppUser user, List<com.pulsefit.domain.DomainEnums.SubscriptionStatus> statuses);
  Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
