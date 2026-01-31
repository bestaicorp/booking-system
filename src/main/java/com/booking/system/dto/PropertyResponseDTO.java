package com.booking.system.dto;

import com.booking.system.enumeration.PropertyType;
import com.booking.system.model.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Property details returned in API responses")
public class PropertyResponseDTO {

    @Schema(description = "Unique property identifier", example = "1")
    private Long id;

    @Schema(description = "Name of the property", example = "Beach House")
    private String name;

    @Schema(description = "Type of the property", example = "HOUSE")
    private PropertyType type;

    public static PropertyResponseDTO of(Property in) {
        PropertyResponseDTO result = new PropertyResponseDTO();
        result.setId(in.getId());
        result.setName(in.getName());
        result.setType(in.getType());
        return result;
    }
}
