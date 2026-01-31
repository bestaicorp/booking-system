package com.booking.system.dto;

import com.booking.system.model.Booking;
import com.booking.system.model.Guest;
import com.booking.system.model.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating or updating a booking")
public class BookingRequestDTO {

    @NotNull(message = "Property ID is required")
    @Positive(message = "Property ID must be a positive number")
    @Schema(description = "ID of the property to book", example = "1")
    private Long propertyId;

    @NotNull(message = "Guest ID is required")
    @Positive(message = "Guest ID must be a positive number")
    @Schema(description = "ID of the guest making the booking", example = "1")
    private Long guestId;

    @NotNull(message = "Start date is required")
    @Schema(description = "Check-in date (inclusive)", example = "2026-06-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Check-out date (exclusive)", example = "2026-06-10")
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
