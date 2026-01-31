package com.booking.system.dto;

import com.booking.system.enumeration.BookingStatus;
import com.booking.system.model.Booking;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Booking details returned in API responses")
public class BookingResponseDTO {

    @Schema(description = "Unique booking identifier", example = "1")
    private Long id;

    @JsonProperty("property")
    @Schema(description = "Property associated with this booking")
    private PropertyResponseDTO propertyDTO;

    @JsonProperty("guest")
    @Schema(description = "Guest who made this booking")
    private GuestResponseDTO guestDTO;

    @Schema(description = "Check-in date (inclusive)", example = "2025-06-01")
    private LocalDate startDate;

    @Schema(description = "Check-out date (exclusive)", example = "2025-06-10")
    private LocalDate endDate;

    @Schema(description = "Current booking status", example = "BOOKED")
    private BookingStatus status;

    public static BookingResponseDTO of(Booking in) {
        BookingResponseDTO result = new BookingResponseDTO();
        result.setId(in.getId());
        result.setPropertyDTO(PropertyResponseDTO.of(in.getProperty()));
        result.setGuestDTO(GuestResponseDTO.of(in.getGuest()));
        result.setStartDate(in.getStartDate());
        result.setEndDate(in.getEndDate());
        result.setStatus(in.getStatus());
        return result;
    }
}
