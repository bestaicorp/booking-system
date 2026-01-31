package com.booking.system.controller;

import com.booking.system.dto.PropertyRequestDTO;
import com.booking.system.dto.PropertyResponseDTO;
import com.booking.system.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "Property management operations")
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Create a property", description = "Creates a new property that can be used for bookings and blocks.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Property created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PropertyResponseDTO> create(@RequestBody @Valid PropertyRequestDTO propertyRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(propertyRequestDTO));
    }

    @Operation(summary = "Get all properties", description = "Retrieves a paginated list of all properties, sorted by newest first.")
    @ApiResponse(responseCode = "200", description = "Properties retrieved successfully")
    @GetMapping
    public Page<PropertyResponseDTO> getAll(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page", example = "20") @RequestParam(defaultValue = "20") @Min(1) int size) {
        return propertyService.getAll(page, size);
    }

    @Operation(summary = "Get a property", description = "Retrieves a property by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property found"),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content)
    })
    @GetMapping("/{id}")
    public PropertyResponseDTO get(@Parameter(description = "Property ID", example = "1") @PathVariable Long id) {
        return propertyService.get(id);
    }

    @Operation(summary = "Update a property", description = "Updates an existing property's name and type.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Property updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content)
    })
    @PutMapping("/{id}")
    public PropertyResponseDTO update(@RequestBody @Valid PropertyRequestDTO propertyRequestDTO,
                                      @Parameter(description = "Property ID", example = "1") @PathVariable Long id) {
        return propertyService.update(propertyRequestDTO, id);
    }

    @Operation(summary = "Delete a property", description = "Permanently deletes a property.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Property deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Property ID", example = "1") @PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
