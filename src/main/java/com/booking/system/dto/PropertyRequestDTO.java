package com.booking.system.dto;

import com.booking.system.enumeration.PropertyType;
import com.booking.system.model.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating or updating a property")
public class PropertyRequestDTO {

    @NotBlank(message = "Property name is required")
    @Schema(description = "Name of the property", example = "Beach House")
    private String name;

    @NotNull(message = "Property type is required")
    @Schema(description = "Type of the property", example = "HOUSE")
    private PropertyType type;

    public static Property toProperty(PropertyRequestDTO in) {
        Property result = new Property();
        result.setName(in.getName());
        result.setType(in.getType());
        return result;
    }
}
