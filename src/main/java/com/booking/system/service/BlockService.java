package com.booking.system.service;

import com.booking.system.dto.BlockRequestDTO;
import com.booking.system.dto.BlockResponseDTO;
import com.booking.system.exception.BlockNotFoundException;
import com.booking.system.exception.PropertyNotFoundException;
import com.booking.system.model.Block;
import com.booking.system.model.Property;
import com.booking.system.repository.BlockRepository;
import com.booking.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BlockService {

    private final BlockRepository blockRepository;
    private final AvailabilityService availabilityService;
    private final DateValidationService dateValidationService;
    private final PropertyRepository propertyRepository;

    public BlockResponseDTO create(BlockRequestDTO blockRequestDTO) {
        log.info("Creating block for property {}, dates: {} - {}",
                blockRequestDTO.getPropertyId(),
                blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate());
        dateValidationService.validate(blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(blockRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(blockRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBlock(property.getId(), blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate(), null);
        Block saved = blockRepository.save(BlockRequestDTO.toBlock(blockRequestDTO, property));
        log.info("Block created successfully with id {}", saved.getId());
        return BlockResponseDTO.of(saved);
    }

    public BlockResponseDTO update(BlockRequestDTO blockRequestDTO, Long id) {
        log.info("Updating block {} for property {}, dates: {} - {}",
                id, blockRequestDTO.getPropertyId(),
                blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate());
        dateValidationService.validate(blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(blockRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(blockRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBlock(blockRequestDTO.getPropertyId(), blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate(), id);
        Block blockDB = findBlock(id);
        Block updated = updateBlock(blockRequestDTO, blockDB, property);
        log.info("Block {} updated successfully", id);
        return BlockResponseDTO.of(updated);
    }

    public void delete(Long blockId) {
        log.info("Deleting block {}", blockId);
        blockRepository.delete(findBlock(blockId));
        log.info("Block {} deleted successfully", blockId);
    }

    private Block findBlock(Long blockId) {
        return blockRepository.findById(blockId).orElseThrow(() -> new BlockNotFoundException(blockId));
    }

    private Block updateBlock(BlockRequestDTO blockRequestDTO, Block block, Property property) {
        block.setStartDate(blockRequestDTO.getStartDate());
        block.setEndDate(blockRequestDTO.getEndDate());
        block.setReason(blockRequestDTO.getReason());
        block.setProperty(property);
        return block;
    }
}
