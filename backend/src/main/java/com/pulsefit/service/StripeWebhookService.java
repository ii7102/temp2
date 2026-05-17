package com.pulsefit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsefit.config.AppProperties;
import com.pulsefit.domain.Booking;
import com.pulsefit.domain.Studio;
import com.pulsefit.domain.DomainEnums.BookingStatus;
import com.pulsefit.domain.DomainEnums.PaymentStatus;
import com.pulsefit.domain.DomainEnums.SubscriptionStatus;
import com.pulsefit.domain.Payment;
import com.pulsefit.domain.StripeEvent;
import com.pulsefit.domain.Subscription;
import com.pulsefit.repository.BookingRepository;
import com.pulsefit.repository.PaymentRepository;
import com.pulsefit.repository.StripeEventRepository;
import com.pulsefit.repository.SubscriptionRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeWebhookService {

  private final AppProperties properties;
  private final StripeEventRepository stripeEventRepository;
  private final PaymentRepository paymentRepository;
  private final BookingRepository bookingRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final ObjectMapper objectMapper;

  public StripeWebhookService(AppProperties properties, StripeEventRepository stripeEventRepository,
      PaymentRepository paymentRepository, BookingRepository bookingRepository,
      SubscriptionRepository subscriptionRepository, ObjectMapper objectMapper) {
    this.properties = properties;
    this.stripeEventRepository = stripeEventRepository;
    this.paymentRepository = paymentRepository;
    this.bookingRepository = bookingRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public void handle(String payload, String signature) throws SignatureVerificationException {
    Event event = Webhook.constructEvent(payload, signature, properties.stripe().webhookSecret());
    if (stripeEventRepository.findByStripeEventId(event.getId()).isPresent()) {
      return;
    }
    switch (event.getType()) {
      case "checkout.session.completed" -> handleCheckoutSessionCompleted(event);
      case "invoice.payment_succeeded" -> handleInvoicePaid(event);
      case "customer.subscription.deleted" -> handleSubscriptionCanceled(event);
      default -> {
      }
    }
    StripeEvent stripeEvent = new StripeEvent();
    stripeEvent.stripeEventId = event.getId();
    stripeEvent.eventType = event.getType();
    stripeEvent.payload = payload;
    stripeEvent.processedAt = Instant.now();
    stripeEventRepository.save(stripeEvent);
  }

  private void handleCheckoutSessionCompleted(Event event) {
    Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
    String type = session.getMetadata().getOrDefault("type", "session");
    if ("session".equals(type)) {
      Booking booking = bookingRepository.findById(java.util.UUID.fromString(session.getMetadata().get("bookingId"))).orElseThrow();
      Payment payment = paymentRepository.findByStripeCheckoutSessionId(session.getId()).orElseThrow();
      payment.status = PaymentStatus.SUCCEEDED;
      payment.stripePaymentIntentId = session.getPaymentIntent();
      paymentRepository.save(payment);
      booking.status = BookingStatus.CONFIRMED;
      booking.session.bookedCount = booking.session.bookedCount + 1;
      booking.payment = payment;
      bookingRepository.save(booking);
      Studio studio = booking.session.fitnessClass.studio;
      studio.earningsTotalEur = studio.earningsTotalEur.add(payment.amountEur.subtract(payment.commissionEur));
    } else if ("subscription".equals(type)) {
      Payment payment = paymentRepository.findByStripeCheckoutSessionId(session.getId()).orElseThrow();
      payment.status = PaymentStatus.SUCCEEDED;
      payment.stripePaymentIntentId = session.getPaymentIntent();
      paymentRepository.save(payment);
      Subscription subscription = subscriptionRepository.findById(java.util.UUID.fromString(session.getMetadata().get("subscriptionId")))
          .orElseThrow();
      subscription.stripeSubscriptionId = session.getSubscription();
      subscription.status = SubscriptionStatus.ACTIVE;
      subscription.creditsBalance = 10;
      subscription.currentPeriodEnd = Instant.now().plusSeconds(30L * 24 * 3600);
      subscriptionRepository.save(subscription);
    }
  }

  private void handleInvoicePaid(Event event) {
    Object data = event.getDataObjectDeserializer().getObject().orElse(null);
    if (data instanceof com.stripe.model.Invoice invoice) {
      subscriptionRepository.findByStripeSubscriptionId(invoice.getSubscription()).ifPresent(subscription -> {
        subscription.status = SubscriptionStatus.ACTIVE;
        subscription.creditsBalance = 10;
        subscription.currentPeriodEnd = Instant.now().plusSeconds(30L * 24 * 3600);
        subscriptionRepository.save(subscription);
      });
    }
  }

  private void handleSubscriptionCanceled(Event event) {
    Object data = event.getDataObjectDeserializer().getObject().orElse(null);
    if (data instanceof com.stripe.model.Subscription stripeSubscription) {
      subscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId()).ifPresent(subscription -> {
        subscription.status = SubscriptionStatus.CANCELED;
        subscription.cancelledAt = Instant.now();
        subscriptionRepository.save(subscription);
      });
    }
  }
}
