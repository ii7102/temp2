package com.pulsefit.repository;

import com.pulsefit.domain.FitnessClass;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FitnessClassRepository extends JpaRepository<FitnessClass, UUID> {}
