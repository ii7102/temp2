package com.pulsefit.service;

import com.pulsefit.config.AppProperties;
import com.pulsefit.domain.AppUser;
import com.pulsefit.domain.Booking;
import com.pulsefit.domain.ClassSession;
import com.pulsefit.domain.Payment;
import com.pulsefit.domain.Subscription;
import com.pulsefit.domain.DomainEnums.BookingStatus;
import com.pulsefit.domain.DomainEnums.PaymentStatus;
import com.pulsefit.domain.DomainEnums.PaymentType;
import com.pulsefit.domain.DomainEnums.SubscriptionStatus;
import com.pulsefit.repository.BookingRepository;
import com.pulsefit.repository.ClassSessionRepository;
import com.pulsefit.repository.PaymentRepository;
import com.pulsefit.repository.SubscriptionRepository;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

  private final AppProperties properties;
  private final CurrentUserService currentUserService;
  private final ClassSessionRepository classSessionRepository;
  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final SubscriptionRepository subscriptionRepository;

  public CheckoutService(AppProperties properties, CurrentUserService currentUserService,
      ClassSessionRepository classSessionRepository, BookingRepository bookingRepository,
      PaymentRepository paymentRepository, SubscriptionRepository subscriptionRepository) {
    this.properties = properties;
    this.currentUserService = currentUserService;
    this.classSessionRepository = classSessionRepository;
    this.bookingRepository = bookingRepository;
    this.paymentRepository = paymentRepository;
    this.subscriptionRepository = subscriptionRepository;
    com.stripe.Stripe.apiKey = properties.stripe().secretKey();
  }

  @Transactional
  public com.pulsefit.web.dto.BillingDtos.CheckoutResponse createSessionCheckout(org.springframework.security.oauth2.jwt.Jwt jwt, java.util.UUID sessionId) throws Exception {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    ClassSession session = classSessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Class session not found"));
    if (session.bookedCount >= session.capacity) {
      throw new IllegalArgumentException("This session is sold out");
    }
    Payment payment = new Payment();
    payment.amountEur = session.priceEur;
    payment.commissionEur = session.priceEur.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
    payment.currency = "eur";
    payment.status = PaymentStatus.PENDING;
    payment.type = PaymentType.SESSION;
    Booking booking = new Booking();
    booking.session = session;
    booking.user = user;
    booking.status = BookingStatus.PENDING_PAYMENT;
    booking.creditsUsed = 0;
    booking.reviewLeft = false;
    booking.bookedAt = java.time.Instant.now();
    booking = bookingRepository.save(booking);
    payment.booking = booking;
    payment.idempotencyKey = "booking:" + booking.id;
    payment = paymentRepository.save(payment);
    booking.payment = payment;
    bookingRepository.save(booking);
    String customerId = ensureCustomer(user);
    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setCustomer(customerId)
        .setSuccessUrl(properties.stripe().publicBaseUrl() + "/dashboard/bookings?checkout=success")
        .setCancelUrl(properties.stripe().publicBaseUrl() + "/checkout/" + session.id + "?canceled=1")
        .addLineItem(SessionCreateParams.LineItem.builder()
            .setQuantity(1L)
            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("eur")
                .setUnitAmount(session.priceEur.multiply(new BigDecimal("100")).longValueExact())
                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(session.fitnessClass.title)
                    .setDescription(session.fitnessClass.studio.name + " · " + session.locationName)
                    .build())
                .build())
            .build())
        .putMetadata("bookingId", booking.id.toString())
        .putMetadata("type", "session")
        .build();
    Session stripeSession = Session.create(params);
    payment.stripeCheckoutSessionId = stripeSession.getId();
    paymentRepository.save(payment);
    return new com.pulsefit.web.dto.BillingDtos.CheckoutResponse(stripeSession.getId(), stripeSession.getUrl());
  }

  @Transactional
  public com.pulsefit.web.dto.BillingDtos.CheckoutResponse createSubscriptionCheckout(org.springframework.security.oauth2.jwt.Jwt jwt) throws Exception {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    String customerId = ensureCustomer(user);
    Subscription subscription = new Subscription();
    subscription.user = user;
    subscription.stripeCustomerId = customerId;
    subscription.priceId = properties.stripe().monthlyPackPriceId();
    subscription.status = SubscriptionStatus.PENDING;
    subscription.creditsBalance = 0;
    subscription = subscriptionRepository.save(subscription);
    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        .setCustomer(customerId)
        .setSuccessUrl(properties.stripe().publicBaseUrl() + "/billing?checkout=success")
        .setCancelUrl(properties.stripe().publicBaseUrl() + "/billing?canceled=1")
        .addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L).setPrice(properties.stripe().monthlyPackPriceId()).build())
        .putMetadata("type", "subscription")
      .putMetadata("subscriptionId", subscription.id.toString())
        .putMetadata("userId", user.id.toString())
        .build();
    Session stripeSession = Session.create(params);
    Payment payment = new Payment();
    payment.amountEur = new BigDecimal("79.00");
    payment.commissionEur = BigDecimal.ZERO;
    payment.currency = "eur";
    payment.status = PaymentStatus.PENDING;
    payment.type = PaymentType.SUBSCRIPTION;
    payment.subscription = subscription;
    payment.stripeCheckoutSessionId = stripeSession.getId();
    payment.idempotencyKey = stripeSession.getId();
    paymentRepository.save(payment);
    return new com.pulsefit.web.dto.BillingDtos.CheckoutResponse(stripeSession.getId(), stripeSession.getUrl());
  }

  @Transactional
  public com.pulsefit.web.dto.BillingDtos.PortalResponse createPortal(org.springframework.security.oauth2.jwt.Jwt jwt) throws Exception {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    String customerId = ensureCustomer(user);
    com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
        .setCustomer(customerId)
        .setReturnUrl(properties.stripe().publicBaseUrl() + "/billing")
        .build();
    com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);
    return new com.pulsefit.web.dto.BillingDtos.PortalResponse(session.getUrl());
  }

  @Transactional
  public void cancelSubscription(org.springframework.security.oauth2.jwt.Jwt jwt) throws Exception {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    Subscription subscription = subscriptionRepository.findByUserAndStatusIn(user, java.util.List.of(SubscriptionStatus.ACTIVE))
        .orElseThrow(() -> new IllegalArgumentException("No active subscription found"));
    if (subscription.stripeSubscriptionId != null && !subscription.stripeSubscriptionId.isBlank()) {
      com.stripe.model.Subscription stripeSubscription = com.stripe.model.Subscription.retrieve(subscription.stripeSubscriptionId);
      stripeSubscription.cancel();
    }
    subscription.status = SubscriptionStatus.CANCELED;
    subscription.cancelledAt = java.time.Instant.now();
    subscriptionRepository.save(subscription);
  }

  private String ensureCustomer(AppUser user) throws Exception {
    if (user.stripeCustomerId != null && !user.stripeCustomerId.isBlank()) {
      return user.stripeCustomerId;
    }
    Customer customer = Customer.create(CustomerCreateParams.builder()
        .setEmail(user.email)
        .setName(user.fullName)
        .build());
    user.stripeCustomerId = customer.getId();
    return customer.getId();
  }
}
