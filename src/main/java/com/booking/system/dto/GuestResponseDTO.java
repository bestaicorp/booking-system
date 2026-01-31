package com.booking.system.dto;

import com.booking.system.model.Guest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Guest details returned in API responses")
public class GuestResponseDTO {

    @Schema(description = "Unique guest identifier", example = "1")
    private Long id;

    @Schema(description = "Full name of the guest", example = "John Doe")
    private String name;

    @Schema(description = "Email address of the guest", example = "john.doe@example.com")
    private String email;

    public static GuestResponseDTO of(Guest in) {
        GuestResponseDTO result = new GuestResponseDTO();
        result.setId(in.getId());
        result.setName(in.getName());
        result.setEmail(in.getEmail());
        return result;
    }
}
