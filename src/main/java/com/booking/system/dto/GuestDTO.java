package com.booking.system.dto;

import com.booking.system.model.Guest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {

    private Long id;

    @NotBlank(message = "Guest name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public static Guest toGuest(final GuestDTO in) {
        Guest result = new Guest();
        result.setName(in.getName());
        result.setEmail(in.getEmail());
        return result;
    }

    public static GuestDTO of(final Guest in) {
        GuestDTO result = new GuestDTO();
        result.setId(in.getId());
        result.setName(in.getName());
        result.setEmail(in.getEmail());
        return result;
    }
}
