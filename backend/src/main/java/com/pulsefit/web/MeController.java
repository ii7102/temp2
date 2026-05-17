package com.pulsefit.web;

import com.pulsefit.service.CheckoutService;
import com.pulsefit.service.MeService;
import com.pulsefit.web.dto.BillingDtos;
import com.pulsefit.web.dto.MeDtos;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/me", "/me"})
public class MeController {

  private final MeService meService;
  private final CheckoutService checkoutService;

  public MeController(MeService meService, CheckoutService checkoutService) {
    this.meService = meService;
    this.checkoutService = checkoutService;
  }

  @GetMapping("/profile")
  MeDtos.ProfileResponse profile(JwtAuthenticationToken authentication) {
    return meService.profile(authentication.getToken());
  }

  @PutMapping("/profile")
  MeDtos.ProfileResponse updateProfile(JwtAuthenticationToken authentication, @Valid @RequestBody MeDtos.ProfileUpdateRequest request) throws Exception {
    return meService.updateProfile(authentication.getToken(), request);
  }

  @GetMapping("/dashboard")
  MeDtos.DashboardResponse dashboard(JwtAuthenticationToken authentication) {
    return meService.dashboard(authentication.getToken());
  }

  @GetMapping("/bookings")
  List<MeDtos.BookingCard> bookings(JwtAuthenticationToken authentication) {
    return meService.bookings(authentication.getToken());
  }

  @PostMapping("/bookings/{sessionId}/checkout")
  BillingDtos.CheckoutResponse createCheckout(JwtAuthenticationToken authentication, @PathVariable UUID sessionId) throws Exception {
    return checkoutService.createSessionCheckout(authentication.getToken(), sessionId);
  }

  @PostMapping("/subscription/checkout")
  BillingDtos.CheckoutResponse createSubscriptionCheckout(JwtAuthenticationToken authentication) throws Exception {
    return checkoutService.createSubscriptionCheckout(authentication.getToken());
  }

  @PostMapping("/subscription/portal")
  BillingDtos.PortalResponse customerPortal(JwtAuthenticationToken authentication) throws Exception {
    return checkoutService.createPortal(authentication.getToken());
  }

  @DeleteMapping("/subscription")
  void cancelSubscription(JwtAuthenticationToken authentication) throws Exception {
    checkoutService.cancelSubscription(authentication.getToken());
  }

  @PostMapping("/bookings/{bookingId}/cancel")
  void cancelBooking(JwtAuthenticationToken authentication, @PathVariable UUID bookingId, @RequestBody MeDtos.BookingCancelRequest request) {
    meService.cancelBooking(authentication.getToken(), bookingId, request.reason());
  }

  @PostMapping("/reviews")
  MeDtos.ReviewCreateRequest review(JwtAuthenticationToken authentication, @Valid @RequestBody MeDtos.ReviewCreateRequest request) {
    return meService.saveReview(authentication.getToken(), request);
  }

  @GetMapping("/billing")
  MeDtos.BillingOverview billing(JwtAuthenticationToken authentication) throws Exception {
    BillingDtos.PortalResponse portal = checkoutService.createPortal(authentication.getToken());
    return meService.billing(authentication.getToken(), portal.url());
  }
}
