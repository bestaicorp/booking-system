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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(summary = "Get all blocks", description = "Retrieves a paginated list of all blocks, sorted by newest first.")
    @ApiResponse(responseCode = "200", description = "Blocks retrieved successfully")
    @GetMapping
    public Page<BlockResponseDTO> getAll(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20") @RequestParam(defaultValue = "20") int size) {
        return blockService.getAll(page, size);
    }

    @Operation(summary = "Get a block", description = "Retrieves a block by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Block found"),
            @ApiResponse(responseCode = "404", description = "Block not found", content = @Content)
    })
    @GetMapping("/{id}")
    public BlockResponseDTO get(@Parameter(description = "Block ID", example = "1") @PathVariable Long id) {
        return blockService.get(id);
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
