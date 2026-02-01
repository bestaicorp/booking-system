package com.booking.system.service;

import com.booking.system.dto.BookingRequestDTO;
import com.booking.system.dto.BookingResponseDTO;
import com.booking.system.exception.BookingNotFoundException;
import com.booking.system.exception.GuestNotFoundException;
import com.booking.system.exception.InvalidBookingStateException;
import com.booking.system.exception.PropertyNotFoundException;
import com.booking.system.model.Booking;
import com.booking.system.model.Guest;
import com.booking.system.model.Property;
import com.booking.system.repository.BookingRepository;
import com.booking.system.repository.GuestRepository;
import com.booking.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.booking.system.enumeration.BookingStatus.CANCELLED;
import static com.booking.system.enumeration.BookingStatus.REBOOKED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilityService availabilityService;
    private final DateValidationService dateValidationService;
    private final PropertyRepository propertyRepository;
    private final GuestRepository guestRepository;

    public BookingResponseDTO create(BookingRequestDTO bookingRequestDTO) {
        log.info("Creating booking for property {} with guest {}, dates: {} - {}",
                bookingRequestDTO.getPropertyId(), bookingRequestDTO.getGuestId(),
                bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate());
        dateValidationService.validate(bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(bookingRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBooking(property.getId(), bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), null);
        Guest guest = guestRepository.findById(bookingRequestDTO.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException(bookingRequestDTO.getGuestId()));
        Booking saved = bookingRepository.save(BookingRequestDTO.toBooking(bookingRequestDTO, property, guest));
        log.info("Booking created successfully with id {}", saved.getId());
        return BookingResponseDTO.of(saved);
    }

    public Page<BookingResponseDTO> getAll(int page, int size) {
        log.debug("Fetching bookings page {} with size {}", page, size);
        return bookingRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(BookingResponseDTO::of);
    }

    public BookingResponseDTO get(Long bookingId) {
        log.debug("Fetching booking with id {}", bookingId);
        Booking bookingDB = findBooking(bookingId);
        return BookingResponseDTO.of(bookingDB);
    }

    public BookingResponseDTO update(BookingRequestDTO bookingRequestDTO, Long id) {
        log.info("Updating booking {} for property {}, dates: {} - {}",
                id, bookingRequestDTO.getPropertyId(),
                bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate());
        Booking bookingDB = findBooking(id);
        if (bookingDB.getStatus() == CANCELLED) {
            throw new InvalidBookingStateException("Cannot update a cancelled booking");
        }
        dateValidationService.validate(bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(bookingRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBooking(bookingRequestDTO.getPropertyId(), bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), id);

        Guest guest = guestRepository.findById(bookingRequestDTO.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException(bookingRequestDTO.getGuestId()));
        Booking updated = updateBooking(bookingRequestDTO, bookingDB, guest, property);
        log.info("Booking {} updated successfully", id);
        return BookingResponseDTO.of(updated);
    }

    public BookingResponseDTO cancel(Long bookingId) {
        log.info("Cancelling booking {}", bookingId);
        Booking bookingDB = findBooking(bookingId);
        if (bookingDB.getStatus() == CANCELLED) {
            throw new InvalidBookingStateException("Booking is already cancelled");
        }
        bookingDB.setStatus(CANCELLED);
        log.info("Booking {} cancelled successfully", bookingId);
        return BookingResponseDTO.of(bookingDB);
    }

    /** Reactivates a cancelled booking. Re-validates dates and checks for overlaps since they may have changed. */
    public BookingResponseDTO rebook(Long bookingId) {
        log.info("Rebooking booking {}", bookingId);
        Booking bookingDB = findBooking(bookingId);
        if (bookingDB.getStatus() != CANCELLED) {
            throw new InvalidBookingStateException("Only cancelled bookings can be rebooked");
        }
        dateValidationService.validate(bookingDB.getStartDate(), bookingDB.getEndDate());
        propertyRepository.findAndLockProperty(bookingDB.getProperty().getId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingDB.getProperty().getId()));

        availabilityService.ensureAvailableForBooking(bookingDB.getProperty().getId(), bookingDB.getStartDate(), bookingDB.getEndDate(), bookingDB.getId());
        bookingDB.setStatus(REBOOKED);
        log.info("Booking {} rebooked successfully", bookingId);
        return BookingResponseDTO.of(bookingDB);
    }

    public void delete(Long bookingId) {
        log.info("Deleting booking {}", bookingId);
        bookingRepository.delete(findBooking(bookingId));
        log.info("Booking {} deleted successfully", bookingId);
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    private Booking updateBooking(BookingRequestDTO bookingRequestDTO, Booking booking, Guest guest, Property property) {
        booking.setGuest(guest);
        booking.setStartDate(bookingRequestDTO.getStartDate());
        booking.setEndDate(bookingRequestDTO.getEndDate());
        booking.setProperty(property);
        return booking;
    }
}
