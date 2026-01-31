package com.booking.system.service;

import com.booking.system.exception.DateAlreadyBookedException;
import com.booking.system.repository.BlockRepository;
import com.booking.system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.booking.system.enumeration.BookingStatus.BOOKED;
import static com.booking.system.enumeration.BookingStatus.REBOOKED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    public void ensureAvailableForBooking(Long propertyId, LocalDate startDate, LocalDate endDate, Long bookingId) {
        log.debug("Checking booking availability for property {}, dates: {} - {}, excludeId: {}",
                propertyId, startDate, endDate, bookingId);
        if (bookingRepository.hasOverlap(propertyId, startDate, endDate, List.of(BOOKED, REBOOKED), bookingId)) {
            log.warn("Booking overlap detected for property {}, dates: {} - {}", propertyId, startDate, endDate);
            throw new DateAlreadyBookedException("The requested dates are already booked for this property");
        }

        if (blockRepository.hasOverlap(propertyId, startDate, endDate, null)) {
            log.warn("Block overlap detected for property {}, dates: {} - {}", propertyId, startDate, endDate);
            throw new DateAlreadyBookedException("The requested dates are blocked for this property");
        }
    }

    public void ensureAvailableForBlock(Long propertyId, LocalDate startDate, LocalDate endDate, Long blockId) {
        log.debug("Checking block availability for property {}, dates: {} - {}, excludeId: {}",
                propertyId, startDate, endDate, blockId);
        if (bookingRepository.hasOverlap(propertyId, startDate, endDate, List.of(BOOKED, REBOOKED), null)) {
            log.warn("Active bookings found for property {}, dates: {} - {}", propertyId, startDate, endDate);
            throw new DateAlreadyBookedException("Cannot block dates that have active bookings");
        }

        if (blockRepository.hasOverlap(propertyId, startDate, endDate, blockId)) {
            log.warn("Block overlap detected for property {}, dates: {} - {}", propertyId, startDate, endDate);
            throw new DateAlreadyBookedException("The requested dates are already blocked for this property");
        }
    }
}
