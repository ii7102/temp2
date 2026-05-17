package com.pulsefit.repository;

import com.pulsefit.domain.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
  Optional<AppUser> findByKeycloakId(String keycloakId);
  Optional<AppUser> findByEmail(String email);
}
