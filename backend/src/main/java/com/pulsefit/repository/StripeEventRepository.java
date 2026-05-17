package com.pulsefit.repository;

import com.pulsefit.domain.StripeEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeEventRepository extends JpaRepository<StripeEvent, UUID> {
  Optional<StripeEvent> findByStripeEventId(String stripeEventId);
}
