package com.booking.system.controller;

import com.booking.system.dto.PropertyRequestDTO;
import com.booking.system.dto.PropertyResponseDTO;
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
    public ResponseEntity<PropertyResponseDTO> create(@RequestBody @Valid PropertyRequestDTO propertyRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(propertyRequestDTO));
    }

    @PutMapping("/{id}")
    public PropertyResponseDTO update(@RequestBody @Valid PropertyRequestDTO propertyRequestDTO, @PathVariable Long id) {
        return propertyService.update(propertyRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
