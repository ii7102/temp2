package com.pulsefit.service;

import com.pulsefit.domain.ClassSession;
import com.pulsefit.domain.FitnessClass;
import com.pulsefit.domain.Instructor;
import com.pulsefit.domain.Review;
import com.pulsefit.domain.Studio;
import com.pulsefit.repository.ClassSessionRepository;
import com.pulsefit.repository.InstructorRepository;
import com.pulsefit.repository.ReviewRepository;
import com.pulsefit.repository.StudioRepository;
import com.pulsefit.web.dto.PublicDtos;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogService {

  private final StudioRepository studioRepository;
  private final InstructorRepository instructorRepository;
  private final ClassSessionRepository classSessionRepository;
  private final ReviewRepository reviewRepository;

  public CatalogService(StudioRepository studioRepository, InstructorRepository instructorRepository,
      ClassSessionRepository classSessionRepository, ReviewRepository reviewRepository) {
    this.studioRepository = studioRepository;
    this.instructorRepository = instructorRepository;
    this.classSessionRepository = classSessionRepository;
    this.reviewRepository = reviewRepository;
  }

  public PublicDtos.LandingResponse landing() {
    List<PublicDtos.StudioSummary> featuredStudios = studioRepository.findByApprovedTrue().stream()
        .filter(studio -> studio.featured)
        .map(this::toStudioSummary)
        .toList();
    List<PublicDtos.SessionSummary> upcomingSessions = upcomingSessions(null, null, null, null, null, 6);
    List<PublicDtos.ReviewSummary> latestReviews = reviewRepository.findAll().stream()
        .sorted(Comparator.comparing((Review review) -> review.createdAt).reversed())
        .limit(3)
        .map(this::toReviewSummary)
        .toList();
    return new PublicDtos.LandingResponse(featuredStudios, upcomingSessions, latestReviews);
  }

  public List<PublicDtos.StudioSummary> studios(String search, String neighborhood, String discipline) {
    String searchTerm = search == null ? "" : search.toLowerCase(Locale.ROOT);
    return studioRepository.findByApprovedTrue().stream()
        .filter(studio -> neighborhood == null || neighborhood.isBlank() || studio.neighborhood.equalsIgnoreCase(neighborhood))
        .filter(studio -> discipline == null || discipline.isBlank() || studio.discipline.equalsIgnoreCase(discipline))
        .filter(studio -> searchTerm.isBlank() || studio.name.toLowerCase(Locale.ROOT).contains(searchTerm)
            || studio.description.toLowerCase(Locale.ROOT).contains(searchTerm)
            || studio.discipline.toLowerCase(Locale.ROOT).contains(searchTerm))
        .map(this::toStudioSummary)
        .toList();
  }

  public PublicDtos.StudioDetailResponse studioDetail(String slug) {
    Studio studio = studioRepository.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Studio not found"));
    List<Instructor> instructors = instructorRepository.findAll().stream().filter(instructor -> instructor.studio.id.equals(studio.id)).toList();
    List<PublicDtos.SessionSummary> sessions = upcomingSessions(slug, null, null, null, null, 10);
    List<PublicDtos.ReviewSummary> reviews = reviewRepository.findAll().stream()
        .filter(review -> review.studio.id.equals(studio.id))
        .sorted(Comparator.comparing((Review review) -> review.createdAt).reversed())
        .map(this::toReviewSummary)
        .toList();
    return new PublicDtos.StudioDetailResponse(
        toStudioSummary(studio),
        instructors.stream().map(this::toInstructorSummary).toList(),
        sessions,
        reviews);
  }

  public List<PublicDtos.SessionSummary> upcomingSessions(String studioSlug, String neighborhood, String discipline,
      String timeWindow, Double maxPrice, int limit) {
    Instant now = Instant.now();
    return classSessionRepository.findByStartsAtAfterOrderByStartsAtAsc(now).stream()
        .filter(session -> session.status.name().equals("PUBLISHED"))
        .filter(session -> studioSlug == null || session.fitnessClass.studio.slug.equals(studioSlug))
        .filter(session -> neighborhood == null || neighborhood.isBlank() || session.fitnessClass.studio.neighborhood.equalsIgnoreCase(neighborhood))
        .filter(session -> discipline == null || discipline.isBlank() || session.fitnessClass.discipline.equalsIgnoreCase(discipline))
        .filter(session -> maxPrice == null || session.priceEur.doubleValue() <= maxPrice)
        .sorted(Comparator.comparing(session -> session.startsAt))
        .limit(limit)
        .map(this::toSessionSummary)
        .toList();
  }

  public List<PublicDtos.SessionSummary> allUpcomingSessions(String search, String neighborhood, String discipline,
      String timeWindow, Double maxPrice) {
    String searchTerm = search == null ? "" : search.toLowerCase(Locale.ROOT);
    return upcomingSessions(null, neighborhood, discipline, timeWindow, maxPrice, 100).stream()
        .filter(session -> searchTerm.isBlank() || session.classTitle().toLowerCase(Locale.ROOT).contains(searchTerm)
            || session.studioName().toLowerCase(Locale.ROOT).contains(searchTerm))
        .toList();
  }

  private PublicDtos.StudioSummary toStudioSummary(Studio studio) {
    return new PublicDtos.StudioSummary(studio.id, studio.name, studio.slug, studio.neighborhood, studio.discipline,
        studio.description, studio.priceFromEur, studio.rating, studio.reviewCount, studio.featured,
        studio.imageUrl, studio.bannerUrl, studio.ownerName, studio.cancellationPolicyHours);
  }

  private PublicDtos.InstructorSummary toInstructorSummary(Instructor instructor) {
    return new PublicDtos.InstructorSummary(instructor.id, instructor.fullName, instructor.bio, instructor.avatarUrl, instructor.specialties);
  }

  private PublicDtos.SessionSummary toSessionSummary(ClassSession session) {
    FitnessClass fitnessClass = session.fitnessClass;
    return new PublicDtos.SessionSummary(session.id, fitnessClass.studio.name, fitnessClass.studio.slug,
        fitnessClass.title, fitnessClass.discipline, session.startsAt, session.endsAt, session.capacity,
        session.bookedCount, session.locationName, session.priceEur, fitnessClass.studio.imageUrl);
  }

  private PublicDtos.ReviewSummary toReviewSummary(Review review) {
    return new PublicDtos.ReviewSummary(review.id, review.user.fullName, review.rating, review.comment, review.createdAt);
  }
}
