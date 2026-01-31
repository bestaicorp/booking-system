package com.booking.system.dto;

import com.booking.system.model.Block;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Block details returned in API responses")
public class BlockResponseDTO {

    @Schema(description = "Unique block identifier", example = "1")
    private Long id;

    @JsonProperty("property")
    @Schema(description = "Property associated with this block")
    private PropertyResponseDTO propertyDTO;

    @Schema(description = "Block start date (inclusive)", example = "2025-07-01")
    private LocalDate startDate;

    @Schema(description = "Block end date (exclusive)", example = "2025-07-15")
    private LocalDate endDate;

    @Schema(description = "Reason for blocking the property", example = "Maintenance work")
    private String reason;

    public static BlockResponseDTO of(Block in) {
        BlockResponseDTO result = new BlockResponseDTO();
        result.setId(in.getId());
        result.setPropertyDTO(PropertyResponseDTO.of(in.getProperty()));
        result.setStartDate(in.getStartDate());
        result.setEndDate(in.getEndDate());
        result.setReason(in.getReason());
        return result;
    }
}
