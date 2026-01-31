package com.booking.system.dto;

import com.booking.system.model.Booking;
import com.booking.system.model.Guest;
import com.booking.system.model.Property;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Guest ID is required")
    private Long guestId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public static Booking toBooking(BookingRequestDTO in, Property property, Guest guest) {
        Booking result = new Booking();
        result.setProperty(property);
        result.setGuest(guest);
        result.setStartDate(in.getStartDate());
        result.setEndDate(in.getEndDate());
        return result;
    }
}

