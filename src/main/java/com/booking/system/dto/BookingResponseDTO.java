package com.booking.system.dto;

import com.booking.system.enumeration.BookingStatus;
import com.booking.system.model.Booking;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {

    private Long id;

    @JsonProperty("property")
    private PropertyDTO propertyDTO;

    @JsonProperty("guest")
    private GuestDTO guestDTO;

    private LocalDate startDate;

    private LocalDate endDate;

    private BookingStatus status;

    public static BookingResponseDTO of(final Booking in) {
        BookingResponseDTO result = new BookingResponseDTO();
        result.setId(in.getId());
        result.setPropertyDTO(PropertyDTO.of(in.getProperty()));
        result.setGuestDTO(GuestDTO.of(in.getGuest()));
        result.setStartDate(in.getStartDate());
        result.setEndDate(in.getEndDate());
        result.setStatus(in.getStatus());
        return result;
    }
}

