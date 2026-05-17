package com.pulsefit.service;

import com.pulsefit.domain.AppUser;
import com.pulsefit.domain.Booking;
import com.pulsefit.domain.DomainEnums.BookingStatus;
import com.pulsefit.domain.DomainEnums.SubscriptionStatus;
import com.pulsefit.domain.Payment;
import com.pulsefit.domain.Review;
import com.pulsefit.domain.Subscription;
import com.pulsefit.repository.BookingRepository;
import com.pulsefit.repository.PaymentRepository;
import com.pulsefit.repository.ReviewRepository;
import com.pulsefit.repository.StudioRepository;
import com.pulsefit.repository.SubscriptionRepository;
import com.pulsefit.web.dto.MeDtos;
import com.pulsefit.web.dto.PublicDtos;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeService {

  private final CurrentUserService currentUserService;
  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final ReviewRepository reviewRepository;
  private final StudioRepository studioRepository;
  private final CatalogService catalogService;

  public MeService(CurrentUserService currentUserService, BookingRepository bookingRepository,
      PaymentRepository paymentRepository, SubscriptionRepository subscriptionRepository,
      ReviewRepository reviewRepository, StudioRepository studioRepository, CatalogService catalogService) {
    this.currentUserService = currentUserService;
    this.bookingRepository = bookingRepository;
    this.paymentRepository = paymentRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.reviewRepository = reviewRepository;
    this.studioRepository = studioRepository;
    this.catalogService = catalogService;
  }

  @Transactional
  public MeDtos.ProfileResponse profile(org.springframework.security.oauth2.jwt.Jwt jwt) {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    return toProfileResponse(user);
  }

  @Transactional
  public MeDtos.ProfileResponse updateProfile(org.springframework.security.oauth2.jwt.Jwt jwt, MeDtos.ProfileUpdateRequest request) throws Exception {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    user.fullName = request.fullName();
    user.email = request.email();
    user.city = request.city();
    user.neighborhood = request.neighborhood();
    user.marketingOptIn = request.marketingOptIn();
    currentUserService.save(user);
    return toProfileResponse(user);
  }

  @Transactional
  public MeDtos.DashboardResponse dashboard(org.springframework.security.oauth2.jwt.Jwt jwt) {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    List<Booking> bookings = bookingRepository.findByUserOrderByBookedAtDesc(user);
    List<MeDtos.BookingCard> cards = bookings.stream().map(this::toBookingCard).toList();
    Optional<Booking> nextBooking = bookings.stream()
        .filter(booking -> booking.status == BookingStatus.CONFIRMED || booking.status == BookingStatus.PENDING_PAYMENT)
        .filter(booking -> booking.session.startsAt.isAfter(Instant.now()))
        .min(Comparator.comparing(booking -> booking.session.startsAt));
    MeDtos.BookingCard nextClass = nextBooking.map(this::toBookingCard).orElse(null);
    Subscription subscription = subscriptionRepository.findByUserAndStatusIn(user, List.of(SubscriptionStatus.ACTIVE)).orElse(null);
    MeDtos.CreditPackCard creditPack = new MeDtos.CreditPackCard(subscription == null ? 0 : subscription.creditsBalance,
        subscription == null ? null : subscription.creditsResetAt, subscription == null ? "None" : subscription.status.name());
    long classesAttended = bookingRepository.countByUserAndStatusIn(user, List.of(BookingStatus.COMPLETED));
    BigDecimal amountSpent = paymentRepository.findAll().stream()
        .filter(payment -> payment.booking != null && payment.booking.user.id.equals(user.id))
        .filter(payment -> payment.status.name().equals("SUCCEEDED"))
        .map(payment -> payment.amountEur)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    List<MeDtos.BookingCard> upcoming = bookings.stream()
        .filter(booking -> booking.session.startsAt.isAfter(Instant.now()))
        .map(this::toBookingCard)
        .toList();
    List<MeDtos.BookingCard> past = bookings.stream()
        .filter(booking -> booking.session.startsAt.isBefore(Instant.now()))
        .map(this::toBookingCard)
        .toList();
    List<PublicDtos.StudioSummary> recommended = catalogService.studios(null, null, null).stream().limit(3).toList();
    return new MeDtos.DashboardResponse(toProfileResponse(user), nextClass, creditPack, (int) classesAttended,
        amountSpent, upcoming, past, recommended);
  }

  @Transactional
  public List<MeDtos.BookingCard> bookings(org.springframework.security.oauth2.jwt.Jwt jwt) {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    return bookingRepository.findByUserOrderByBookedAtDesc(user).stream().map(this::toBookingCard).toList();
  }

  @Transactional
  public void cancelBooking(org.springframework.security.oauth2.jwt.Jwt jwt, UUID bookingId, String reason) {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    if (!booking.user.id.equals(user.id)) {
      throw new IllegalArgumentException("Booking not found");
    }
    if (Instant.now().isAfter(booking.session.startsAt.minusSeconds((long) booking.session.fitnessClass.studio.cancellationPolicyHours * 3600))) {
      throw new IllegalArgumentException("Cancellation window has closed");
    }
    booking.status = BookingStatus.CANCELLED;
    booking.cancelledAt = Instant.now();
    booking.cancellationReason = reason;
    bookingRepository.save(booking);
  }

  @Transactional
  public MeDtos.BillingOverview billing(org.springframework.security.oauth2.jwt.Jwt jwt, String customerPortalUrl) {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    Subscription subscription = subscriptionRepository.findByUserAndStatusIn(user, List.of(SubscriptionStatus.ACTIVE)).orElse(null);
    List<MeDtos.ReceiptCard> receipts = paymentRepository.findTop10ByOrderByCreatedAtDesc().stream()
        .filter(payment -> payment.booking != null && payment.booking.user.id.equals(user.id)
            || payment.subscription != null && payment.subscription.user.id.equals(user.id))
        .map(payment -> new MeDtos.ReceiptCard(payment.id, payment.type.name(), payment.status.name(), payment.amountEur,
            payment.currency, payment.createdAt,
            payment.booking != null ? payment.booking.session.fitnessClass.title : "10-class pack"))
        .toList();
    return new MeDtos.BillingOverview(toProfileResponse(user),
        new MeDtos.CreditPackCard(subscription == null ? 0 : subscription.creditsBalance,
            subscription == null ? null : subscription.creditsResetAt,
            subscription == null ? "None" : subscription.status.name()),
        receipts, customerPortalUrl);
  }

  @Transactional
  public MeDtos.ReviewCreateRequest saveReview(org.springframework.security.oauth2.jwt.Jwt jwt, MeDtos.ReviewCreateRequest request) {
    AppUser user = currentUserService.resolveCurrentUser(jwt);
    Booking booking = bookingRepository.findById(request.bookingId()).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    if (!booking.user.id.equals(user.id)) {
      throw new IllegalArgumentException("Booking not found");
    }
    Review review = new Review();
    review.booking = booking;
    review.user = user;
    review.studio = booking.session.fitnessClass.studio;
    review.rating = request.rating();
    review.comment = request.comment();
    review.createdAt = Instant.now();
    reviewRepository.save(review);
    booking.reviewLeft = true;
    bookingRepository.save(booking);
    return request;
  }

  private MeDtos.ProfileResponse toProfileResponse(AppUser user) {
    boolean hasSubscription = subscriptionRepository.findByUserAndStatusIn(user, List.of(SubscriptionStatus.ACTIVE)).isPresent();
    return new MeDtos.ProfileResponse(user.id, user.fullName, user.email, user.city, user.neighborhood,
        user.roleName.name(), user.marketingOptIn, user.savedPaymentMethodBrand, user.savedPaymentMethodLast4, hasSubscription);
  }

  private MeDtos.BookingCard toBookingCard(Booking booking) {
    return new MeDtos.BookingCard(booking.id, booking.session.id, booking.session.fitnessClass.title,
        booking.session.fitnessClass.studio.name, booking.session.fitnessClass.discipline, booking.session.startsAt,
        booking.session.endsAt, booking.status.name(), booking.session.locationName, booking.session.priceEur, booking.reviewLeft);
  }
}
