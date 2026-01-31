package com.booking.system.dto;

import com.booking.system.enumeration.PropertyType;
import com.booking.system.model.Property;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequestDTO {

    @NotBlank(message = "Property name is required")
    private String name;

    @NotNull(message = "Property type is required")
    private PropertyType type;

    public static Property toProperty(PropertyRequestDTO in) {
        Property result = new Property();
        result.setName(in.getName());
        result.setType(in.getType());
        return result;
    }
}