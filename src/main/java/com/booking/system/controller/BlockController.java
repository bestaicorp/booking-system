package com.booking.system.controller;

import com.booking.system.dto.BlockRequestDTO;
import com.booking.system.dto.BlockResponseDTO;
import com.booking.system.service.BlockService;
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
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockResponseDTO> create(final @RequestBody @Valid BlockRequestDTO blockRequestDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(blockService.create(blockRequestDTO));
    }

    @PutMapping("/{id}")
    public BlockResponseDTO update(final @RequestBody @Valid BlockRequestDTO blockRequestDTO, @PathVariable Long id) {

        return blockService.update(blockRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        blockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
