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
    private PropertyResponseDTO propertyDTO;

    @JsonProperty("guest")
    private GuestResponseDTO guestDTO;

    private LocalDate startDate;

    private LocalDate endDate;

    private BookingStatus status;

    public static BookingResponseDTO of(final Booking in) {
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

