package com.booking.system.repository;

import com.booking.system.enumeration.BookingStatus;
import com.booking.system.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.property.id = :propertyId " +
            "AND b.startDate < :endDate " +
            "AND b.endDate > :startDate " +
            "AND b.status IN :statuses " +
            "AND (:excludeId IS NULL OR b.id != :excludeId)")
    boolean hasOverlap(@Param("propertyId") Long propertyId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("statuses") List<BookingStatus> statuses,
                       @Param("excludeId") Long excludeId);
}