package com.booking.system.controller;

import com.booking.system.dto.GuestRequestDTO;
import com.booking.system.dto.GuestResponseDTO;
import com.booking.system.service.GuestService;
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
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
@Tag(name = "Guests", description = "Guest management operations")
public class GuestController {

    private final GuestService guestService;

    @Operation(summary = "Create a guest", description = "Creates a new guest that can be associated with bookings.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Guest created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<GuestResponseDTO> create(@RequestBody @Valid GuestRequestDTO guestRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.create(guestRequestDTO));
    }

    @Operation(summary = "Get all guests", description = "Retrieves a paginated list of all guests, sorted by newest first.")
    @ApiResponse(responseCode = "200", description = "Guests retrieved successfully")
    @GetMapping
    public Page<GuestResponseDTO> getAll(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20") @RequestParam(defaultValue = "20") int size) {
        return guestService.getAll(page, size);
    }

    @Operation(summary = "Get a guest", description = "Retrieves a guest by their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guest found"),
            @ApiResponse(responseCode = "404", description = "Guest not found", content = @Content)
    })
    @GetMapping("/{id}")
    public GuestResponseDTO get(@Parameter(description = "Guest ID", example = "1") @PathVariable Long id) {
        return guestService.get(id);
    }

    @Operation(summary = "Update a guest", description = "Updates an existing guest's name and email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guest updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Guest not found", content = @Content)
    })
    @PutMapping("/{id}")
    public GuestResponseDTO update(@RequestBody @Valid GuestRequestDTO guestRequestDTO,
                                   @Parameter(description = "Guest ID", example = "1") @PathVariable Long id) {
        return guestService.update(guestRequestDTO, id);
    }

    @Operation(summary = "Delete a guest", description = "Permanently deletes a guest.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Guest deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Guest not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Guest ID", example = "1") @PathVariable Long id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
