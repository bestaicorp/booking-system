package com.booking.system.controller;

import com.booking.system.dto.GuestDTO;
import com.booking.system.service.GuestService;
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
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestDTO> create(final @RequestBody @Valid GuestDTO guestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.create(guestDTO));
    }

    @PutMapping
    public GuestDTO update(final @RequestBody @Valid GuestDTO guestDTO) {
        return guestService.update(guestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
