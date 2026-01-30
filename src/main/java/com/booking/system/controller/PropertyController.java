package com.booking.system.controller;

import com.booking.system.dto.PropertyDTO;
import com.booking.system.service.PropertyService;
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
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyDTO> create(final @RequestBody @Valid PropertyDTO propertyDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(propertyDTO));
    }

    @PutMapping
    public PropertyDTO update(final @RequestBody @Valid PropertyDTO propertyDTO) {
        return propertyService.update(propertyDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
