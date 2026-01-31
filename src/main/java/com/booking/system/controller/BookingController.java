package com.booking.system.controller;

import com.booking.system.dto.BookingRequestDTO;
import com.booking.system.dto.BookingResponseDTO;
import com.booking.system.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management operations")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Create a booking", description = "Creates a new booking for a property. Validates date range and checks for overlaps with existing bookings and blocks.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or date range", content = @Content),
            @ApiResponse(responseCode = "404", description = "Property or guest not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Date overlap with existing booking or block", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(bookingRequestDTO));
    }

    @Operation(summary = "Get a booking", description = "Retrieves a booking by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    @GetMapping("/{id}")
    public BookingResponseDTO get(@Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        return bookingService.get(id);
    }

    @Operation(summary = "Update a booking", description = "Updates an existing booking. Cannot update a cancelled booking. Validates date range and checks for overlaps.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or date range", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking, property, or guest not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Booking is cancelled or date overlap exists", content = @Content)
    })
    @PutMapping("/{id}")
    public BookingResponseDTO update(@RequestBody @Valid BookingRequestDTO bookingRequestDTO,
                                     @Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        return bookingService.update(bookingRequestDTO, id);
    }

    @Operation(summary = "Cancel a booking", description = "Cancels an active booking. Already cancelled bookings cannot be cancelled again.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Booking is already cancelled", content = @Content)
    })
    @PatchMapping("/{id}/cancel")
    public BookingResponseDTO cancel(@Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        return bookingService.cancel(id);
    }

    @Operation(summary = "Rebook a cancelled booking", description = "Reactivates a previously cancelled booking. Only cancelled bookings can be rebooked. Validates dates are still valid and checks for overlaps.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking rebooked successfully"),
            @ApiResponse(responseCode = "400", description = "Booking dates are no longer valid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Booking is not cancelled or date overlap exists", content = @Content)
    })
    @PatchMapping("/{id}/rebook")
    public BookingResponseDTO rebook(@Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        return bookingService.rebook(id);
    }

    @Operation(summary = "Delete a booking", description = "Permanently deletes a booking regardless of its status.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Booking ID", example = "1") @PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
