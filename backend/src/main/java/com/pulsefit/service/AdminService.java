package com.pulsefit.service;

import com.pulsefit.domain.DomainEnums.PaymentStatus;
import com.pulsefit.domain.DomainEnums.BookingStatus;
import com.pulsefit.repository.BookingRepository;
import com.pulsefit.repository.PaymentRepository;
import com.pulsefit.repository.StudioRepository;
import com.pulsefit.repository.SubscriptionRepository;
import com.pulsefit.repository.AppUserRepository;
import com.pulsefit.config.AppProperties;
import com.pulsefit.web.dto.BillingDtos;
import com.pulsefit.web.dto.AdminDtos;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {

  private final StudioRepository studioRepository;
  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final AppUserRepository appUserRepository;
  private final KeycloakAdminService keycloakAdminService;
  private final AppProperties appProperties;

  public AdminService(StudioRepository studioRepository, BookingRepository bookingRepository,
      PaymentRepository paymentRepository, SubscriptionRepository subscriptionRepository,
      AppUserRepository appUserRepository, KeycloakAdminService keycloakAdminService,
      AppProperties appProperties) {
    this.studioRepository = studioRepository;
    this.bookingRepository = bookingRepository;
    this.paymentRepository = paymentRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.appUserRepository = appUserRepository;
    this.keycloakAdminService = keycloakAdminService;
    this.appProperties = appProperties;
  }

  public AdminDtos.AdminDashboardResponse dashboard() {
    BigDecimal totalRevenue = paymentRepository.findAll().stream()
        .filter(payment -> payment.status == PaymentStatus.SUCCEEDED)
        .map(payment -> payment.amountEur)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal commission = paymentRepository.findAll().stream()
        .filter(payment -> payment.status == PaymentStatus.SUCCEEDED)
        .map(payment -> payment.commissionEur)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    int activeBookings = (int) bookingRepository.countByStatusIn(List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED));
    int pendingApprovals = (int) studioRepository.findAll().stream().filter(studio -> !studio.approved).count();
    AdminDtos.MetricsResponse metrics = new AdminDtos.MetricsResponse(totalRevenue, activeBookings, pendingApprovals,
        99.9, commission, (int) appUserRepository.count(), (int) studioRepository.count());
    List<AdminDtos.ApprovalCard> approvals = studioRepository.findAll().stream()
        .filter(studio -> !studio.approved)
        .map(studio -> new AdminDtos.ApprovalCard(studio.id, studio.name, studio.discipline, studio.neighborhood,
            studio.createdAt, studio.approved ? "Approved" : "Pending", studio.featured))
        .toList();
    List<AdminDtos.SystemAlert> alerts = List.of(
        new AdminDtos.SystemAlert("API Latency increase - Stripe", "Payment gateway latency elevated by 150ms over the last hour.", "warning", Instant.now().minusSeconds(600)),
        new AdminDtos.SystemAlert("New studio submitted", "Awaiting manual approval in the review queue.", "info", Instant.now().minusSeconds(14400)));
    return new AdminDtos.AdminDashboardResponse(metrics, approvals, alerts);
  }

  public AdminDtos.PaginatedUsersResponse users(String query, int page, int size) {
    List<AdminDtos.KeycloakUserSummary> items = keycloakAdminService.searchUsers(query, page, size);
    return new AdminDtos.PaginatedUsersResponse(items, page, size, items.size());
  }

  public void updateUserRole(String keycloakUserId, String role) {
    keycloakAdminService.updateUserRole(keycloakUserId, role.toLowerCase());
  }

  public List<AdminDtos.PaymentRow> payments() {
    return paymentRepository.findTop10ByOrderByCreatedAtDesc().stream()
        .map(payment -> new AdminDtos.PaymentRow(payment.id, payment.type.name(), payment.status.name(), payment.amountEur,
            payment.booking != null ? payment.booking.user.email : (payment.subscription != null ? payment.subscription.user.email : "unknown"),
            payment.createdAt, payment.booking != null ? payment.booking.session.fitnessClass.title : "Subscription"))
        .toList();
  }

  @Transactional
  public BillingDtos.RefundResponse refund(String paymentId) throws Exception {
    var payment = paymentRepository.findById(java.util.UUID.fromString(paymentId)).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    if (payment.stripePaymentIntentId == null || payment.stripePaymentIntentId.isBlank()) {
      throw new IllegalArgumentException("Payment cannot be refunded through Stripe");
    }
    com.stripe.Stripe.apiKey = appProperties.stripe().secretKey();
    com.stripe.model.Refund refund = com.stripe.model.Refund.create(
        com.stripe.param.RefundCreateParams.builder().setPaymentIntent(payment.stripePaymentIntentId).build());
    payment.status = PaymentStatus.REFUNDED;
    paymentRepository.save(payment);
    return new BillingDtos.RefundResponse(payment.id.toString(), refund.getStatus());
  }
}
