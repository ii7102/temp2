package com.pulsefit.repository;

import com.pulsefit.domain.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  List<Review> findByStudioSlugOrderByCreatedAtDesc(String slug);
}
