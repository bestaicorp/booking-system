package com.booking.system.dto;

import com.booking.system.model.Guest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating or updating a guest")
public class GuestRequestDTO {

    @NotBlank(message = "Guest name is required")
    @Schema(description = "Full name of the guest", example = "John Doe")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Email address of the guest", example = "john.doe@example.com")
    private String email;

    public static Guest toGuest(GuestRequestDTO in) {
        Guest result = new Guest();
        result.setName(in.getName());
        result.setEmail(in.getEmail());
        return result;
    }
}
