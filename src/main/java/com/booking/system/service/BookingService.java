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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.booking.system.enumeration.BookingStatus.CANCELLED;
import static com.booking.system.enumeration.BookingStatus.REBOOKED;

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
        dateValidationService.validate(bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(bookingRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBooking(property.getId(), bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), null);
        Guest guest = guestRepository.findById(bookingRequestDTO.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException(bookingRequestDTO.getGuestId()));
        return BookingResponseDTO.of(bookingRepository.save(BookingRequestDTO.toBooking(bookingRequestDTO, property, guest)));
    }

    public BookingResponseDTO get(final Long bookingId) {
        Booking bookingDB = findBooking(bookingId);
        return BookingResponseDTO.of(bookingDB);
    }

    public BookingResponseDTO update(BookingRequestDTO bookingRequestDTO, Long id) {
        dateValidationService.validate(bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(bookingRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBooking(bookingRequestDTO.getPropertyId(), bookingRequestDTO.getStartDate(), bookingRequestDTO.getEndDate(), id);
        Booking bookingDB = findBooking(id);
        if (bookingDB.getStatus() == CANCELLED) {
            throw new InvalidBookingStateException("Cannot update a cancelled booking");
        }

        Guest guest = guestRepository.findById(bookingRequestDTO.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException(bookingRequestDTO.getGuestId()));
        return BookingResponseDTO.of(updateBooking(bookingRequestDTO, bookingDB, guest, property));
    }

    public BookingResponseDTO cancel(final Long bookingId) {
        Booking bookingDB = findBooking(bookingId);
        if (bookingDB.getStatus() == CANCELLED) {
            throw new InvalidBookingStateException("Booking is already cancelled");
        }
        bookingDB.setStatus(CANCELLED);
        return BookingResponseDTO.of(bookingDB);
    }

    public BookingResponseDTO rebook(final Long bookingId) {
        Booking bookingDB = findBooking(bookingId);
        if (bookingDB.getStatus() != CANCELLED) {
            throw new InvalidBookingStateException("Only cancelled bookings can be rebooked");
        }
        propertyRepository.findAndLockProperty(bookingDB.getProperty().getId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingDB.getProperty().getId()));

        availabilityService.ensureAvailableForBooking(bookingDB.getProperty().getId(), bookingDB.getStartDate(), bookingDB.getEndDate(), bookingDB.getId());
        bookingDB.setStatus(REBOOKED);
        return BookingResponseDTO.of(bookingDB);
    }

    public void delete(final Long bookingId) {
        bookingRepository.delete(findBooking(bookingId));
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
