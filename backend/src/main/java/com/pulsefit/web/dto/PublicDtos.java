package com.pulsefit.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class PublicDtos {
  private PublicDtos() {}

  public record StudioSummary(UUID id, String name, String slug, String neighborhood, String discipline,
      String description, BigDecimal priceFromEur, BigDecimal rating, int reviewCount, boolean featured,
      String imageUrl, String bannerUrl, String ownerName, int cancellationPolicyHours) {}

  public record InstructorSummary(UUID id, String fullName, String bio, String avatarUrl, String specialties) {}

  public record SessionSummary(UUID id, String studioName, String studioSlug, String classTitle,
      String discipline, Instant startsAt, Instant endsAt, int capacity, int bookedCount,
      String locationName, BigDecimal priceEur, String imageUrl) {}

  public record ReviewSummary(UUID id, String reviewerName, int rating, String comment, Instant createdAt) {}

  public record StudioDetailResponse(StudioSummary studio, List<InstructorSummary> instructors,
      List<SessionSummary> upcomingSessions, List<ReviewSummary> reviews) {}

  public record LandingResponse(List<StudioSummary> featuredStudios, List<SessionSummary> upcomingSessions,
      List<ReviewSummary> latestReviews) {}
}
