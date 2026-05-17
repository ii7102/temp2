package com.pulsefit.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class MeDtos {
  private MeDtos() {}

  public record ProfileResponse(UUID id, String fullName, String email, String city, String neighborhood,
      String roleName, boolean marketingOptIn, String savedPaymentMethodBrand, String savedPaymentMethodLast4,
      boolean hasSubscription) {}

  public record ProfileUpdateRequest(@NotBlank @Size(max = 255) String fullName,
      @Email @NotBlank @Size(max = 255) String email,
      @Size(max = 120) String city, @Size(max = 120) String neighborhood, boolean marketingOptIn) {}

  public record BookingCard(UUID id, UUID sessionId, String classTitle, String studioName, String discipline,
      Instant startsAt, Instant endsAt, String status, String locationName, BigDecimal priceEur,
      boolean reviewLeft) {}

  public record CreditPackCard(int creditsBalance, Instant creditsResetAt, String status) {}

  public record DashboardResponse(ProfileResponse profile, BookingCard nextClass, CreditPackCard creditPack,
      int classesAttended, BigDecimal amountSpentEur, List<BookingCard> upcomingBookings,
      List<BookingCard> pastBookings, List<PublicDtos.StudioSummary> recommendedStudios) {}

  public record BookingCancelRequest(@NotBlank String reason) {}

  public record ReviewCreateRequest(UUID bookingId, @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(5) int rating,
      @NotBlank @Size(max = 1000) String comment) {}

  public record BillingOverview(ProfileResponse profile, CreditPackCard subscription,
      List<ReceiptCard> receipts, String customerPortalUrl) {}

  public record ReceiptCard(UUID id, String type, String status, BigDecimal amountEur, String currency,
      Instant createdAt, String description) {}
}
