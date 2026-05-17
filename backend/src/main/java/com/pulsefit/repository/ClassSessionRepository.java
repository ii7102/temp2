package com.pulsefit.repository;

import com.pulsefit.domain.ClassSession;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSessionRepository extends JpaRepository<ClassSession, UUID> {
  List<ClassSession> findByStartsAtAfterOrderByStartsAtAsc(java.time.Instant startsAt);
}
