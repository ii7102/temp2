package com.pulsefit.repository;

import com.pulsefit.domain.Studio;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudioRepository extends JpaRepository<Studio, UUID> {
  Optional<Studio> findBySlug(String slug);
  List<Studio> findByApprovedTrue();
}
