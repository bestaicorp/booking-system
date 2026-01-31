package com.booking.system.dto;

import com.booking.system.model.Guest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestResponseDTO {

    private Long id;

    private String name;

    private String email;

    public static GuestResponseDTO of(final Guest in) {
        GuestResponseDTO result = new GuestResponseDTO();
        result.setId(in.getId());
        result.setName(in.getName());
        result.setEmail(in.getEmail());
        return result;
    }
}