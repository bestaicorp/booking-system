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
