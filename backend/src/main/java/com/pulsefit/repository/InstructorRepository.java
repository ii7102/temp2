package com.pulsefit.repository;

import com.pulsefit.domain.Instructor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, UUID> {}
