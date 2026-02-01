package com.booking.system.repository;

import com.booking.system.model.Property;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    /** Loads a property with a pessimistic write lock (SELECT ... FOR UPDATE) to prevent concurrent bookings. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Property p WHERE p.id = :id")
    Optional<Property> findAndLockProperty(@Param("id") Long id);
}
