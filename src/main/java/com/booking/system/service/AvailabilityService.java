package com.booking.system.service;

import com.booking.system.exception.DateAlreadyBookedException;
import com.booking.system.repository.BlockRepository;
import com.booking.system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.booking.system.enumeration.BookingStatus.BOOKED;
import static com.booking.system.enumeration.BookingStatus.REBOOKED;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    public void ensureAvailableForBooking(Long propertyId, LocalDate startDate, LocalDate endDate, Long bookingId) {
        if (bookingRepository.hasOverlap(propertyId, startDate, endDate, List.of(BOOKED, REBOOKED), bookingId)) {
            throw new DateAlreadyBookedException("The requested dates are already booked for this property");
        }

        if (blockRepository.hasOverlap(propertyId, startDate, endDate, null)) {
            throw new DateAlreadyBookedException("The requested dates are blocked for this property");
        }
    }

    public void ensureAvailableForBlock(Long propertyId, LocalDate startDate, LocalDate endDate, Long blockId) {
        if (bookingRepository.hasOverlap(propertyId, startDate, endDate, List.of(BOOKED, REBOOKED), null)) {
            throw new DateAlreadyBookedException("Cannot block dates that have active bookings");
        }

        if (blockRepository.hasOverlap(propertyId, startDate, endDate, blockId)) {
            throw new DateAlreadyBookedException("The requested dates are already blocked for this property");
        }
    }
}
