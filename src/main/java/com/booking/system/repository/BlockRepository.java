package com.booking.system.repository;

import com.booking.system.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("SELECT COUNT(b) > 0 FROM Block b " +
            "WHERE b.property.id = :propertyId " +
            "AND b.startDate < :endDate " +
            "AND b.endDate > :startDate " +
            "AND (:excludeId IS NULL OR b.id != :excludeId)")
    boolean hasOverlap(@Param("propertyId") Long propertyId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("excludeId") Long excludeId);
}
