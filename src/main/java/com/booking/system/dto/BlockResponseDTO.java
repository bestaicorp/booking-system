package com.booking.system.dto;

import com.booking.system.model.Block;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockResponseDTO {

    private Long id;

    @JsonProperty("property")
    private PropertyResponseDTO propertyDTO;

    private LocalDate startDate;

    private LocalDate endDate;

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

