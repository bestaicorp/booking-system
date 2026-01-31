package com.booking.system.dto;

import com.booking.system.enumeration.PropertyType;
import com.booking.system.model.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResponseDTO {

    private Long id;

    private String name;

    private PropertyType type;

    public static PropertyResponseDTO of(Property in) {
        PropertyResponseDTO result = new PropertyResponseDTO();
        result.setId(in.getId());
        result.setName(in.getName());
        result.setType(in.getType());
        return result;
    }
}