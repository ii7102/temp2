package com.pulsefit.repository;

import com.pulsefit.domain.Payment;
import com.pulsefit.domain.DomainEnums.PaymentStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  Optional<Payment> findByStripeCheckoutSessionId(String stripeCheckoutSessionId);
  Optional<Payment> findByEventId(String eventId);
  List<Payment> findTop10ByOrderByCreatedAtDesc();
  long countByStatus(PaymentStatus status);
}
