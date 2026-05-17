package com.pulsefit.repository;

import com.pulsefit.domain.AppUser;
import com.pulsefit.domain.Booking;
import com.pulsefit.domain.DomainEnums.BookingStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
  List<Booking> findByUserOrderByBookedAtDesc(AppUser user);
  long countByUserAndStatusIn(AppUser user, List<BookingStatus> statuses);
  long countByStatusIn(List<BookingStatus> statuses);
}
