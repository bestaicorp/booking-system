package com.booking.system.dto;

import com.booking.system.model.Block;
import com.booking.system.model.Property;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockRequestDTO {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String reason;

    public static Block toBlock(final BlockRequestDTO in, Property property) {
        Block result = new Block();
        result.setProperty(property);
        result.setStartDate(in.getStartDate());
        result.setEndDate(in.getEndDate());
        result.setReason(in.getReason());
        return result;
    }
}

