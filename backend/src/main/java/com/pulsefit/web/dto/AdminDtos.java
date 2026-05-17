package com.pulsefit.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class AdminDtos {
  private AdminDtos() {}

  public record MetricsResponse(BigDecimal totalRevenueEur, int activeBookings, int pendingApprovals,
      double systemHealth, BigDecimal commissionRevenueEur, int totalUsers, int totalStudios) {}

  public record ApprovalCard(UUID id, String name, String category, String location, Instant submittedAt,
      String status, boolean featured) {}

  public record SystemAlert(String title, String message, String severity, Instant timestamp) {}

  public record AdminDashboardResponse(MetricsResponse metrics, List<ApprovalCard> approvalQueue,
      List<SystemAlert> alerts) {}

  public record KeycloakUserSummary(String id, String email, String fullName, List<String> roles, boolean enabled) {}

  public record PaginatedUsersResponse(List<KeycloakUserSummary> items, int page, int size, int total) {}

  public record UpdateRoleRequest(String role) {}

  public record PaymentRow(UUID id, String type, String status, BigDecimal amountEur, String customer,
      Instant createdAt, String reference) {}
}
