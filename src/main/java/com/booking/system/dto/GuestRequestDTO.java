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
public class GuestRequestDTO {

    @NotBlank(message = "Guest name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public static Guest toGuest(GuestRequestDTO in) {
        Guest result = new Guest();
        result.setName(in.getName());
        result.setEmail(in.getEmail());
        return result;
    }
}