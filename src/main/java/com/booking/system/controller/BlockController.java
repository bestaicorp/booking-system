package com.booking.system.controller;

import com.booking.system.dto.BlockRequestDTO;
import com.booking.system.dto.BlockResponseDTO;
import com.booking.system.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blocks")
@RequiredArgsConstructor
@Tag(name = "Blocks", description = "Property block management — block date ranges to prevent bookings")
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "Create a block", description = "Creates a new block for a property. Validates date range and checks for overlaps with existing bookings and blocks.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Block created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or date range", content = @Content),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Date overlap with existing booking or block", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BlockResponseDTO> create(@RequestBody @Valid BlockRequestDTO blockRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blockService.create(blockRequestDTO));
    }

    @Operation(summary = "Update a block", description = "Updates an existing block. Validates date range and checks for overlaps.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Block updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or date range", content = @Content),
            @ApiResponse(responseCode = "404", description = "Block or property not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Date overlap with existing booking or block", content = @Content)
    })
    @PutMapping("/{id}")
    public BlockResponseDTO update(@RequestBody @Valid BlockRequestDTO blockRequestDTO,
                                   @Parameter(description = "Block ID", example = "1") @PathVariable Long id) {
        return blockService.update(blockRequestDTO, id);
    }

    @Operation(summary = "Delete a block", description = "Permanently deletes a block, freeing up the date range for bookings.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Block deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Block not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Block ID", example = "1") @PathVariable Long id) {
        blockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
