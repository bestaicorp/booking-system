package com.booking.system.controller;

import com.booking.system.dto.BookingRequestDTO;
import com.booking.system.dto.BookingResponseDTO;
import com.booking.system.service.BookingService;
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
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(bookingRequestDTO));
    }

    @GetMapping("/{id}")
    public BookingResponseDTO get(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @PutMapping("/{id}")
    public BookingResponseDTO update(@RequestBody @Valid BookingRequestDTO bookingRequestDTO, @PathVariable Long id) {
        return bookingService.update(bookingRequestDTO, id);
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponseDTO cancel(@PathVariable Long id) {
        return bookingService.cancel(id);
    }

    @PatchMapping("/{id}/rebook")
    public BookingResponseDTO rebook(@PathVariable Long id) {
        return bookingService.rebook(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
