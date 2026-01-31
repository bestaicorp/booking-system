package com.booking.system.dto;

import com.booking.system.model.Block;
import com.booking.system.model.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating or updating a block")
public class BlockRequestDTO {

    @NotNull(message = "Property ID is required")
    @Positive(message = "Property ID must be a positive number")
    @Schema(description = "ID of the property to block", example = "1")
    private Long propertyId;

    @NotNull(message = "Start date is required")
    @Schema(description = "Block start date (inclusive)", example = "2026-07-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Block end date (exclusive)", example = "2026-07-15")
    private LocalDate endDate;

    @Size(max = 255, message = "Reason must not exceed 255 characters")
    @Schema(description = "Reason for blocking the property", example = "Maintenance work")
    private String reason;

    public static Block toBlock(BlockRequestDTO in, Property property) {
        Block result = new Block();
        result.setProperty(property);
        result.setStartDate(in.getStartDate());
        result.setEndDate(in.getEndDate());
        result.setReason(in.getReason());
        return result;
    }
}
