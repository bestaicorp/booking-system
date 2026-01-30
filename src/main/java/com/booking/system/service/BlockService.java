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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockService {

    private final BlockRepository blockRepository;
    private final AvailabilityService availabilityService;
    private final DateValidationService dateValidationService;
    private final PropertyRepository propertyRepository;

    public BlockResponseDTO create(BlockRequestDTO blockRequestDTO) {
        dateValidationService.validate(blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(blockRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(blockRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBlock(property.getId(), blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate(), null);
        return BlockResponseDTO.of(blockRepository.save(BlockRequestDTO.toBlock(blockRequestDTO, property)));
    }

    public BlockResponseDTO update(BlockRequestDTO blockRequestDTO, Long id) {
        dateValidationService.validate(blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate());
        Property property = propertyRepository.findAndLockProperty(blockRequestDTO.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(blockRequestDTO.getPropertyId()));
        availabilityService.ensureAvailableForBlock(blockRequestDTO.getPropertyId(), blockRequestDTO.getStartDate(), blockRequestDTO.getEndDate(), id);
        Block blockDB = findBlock(id);
        return BlockResponseDTO.of(updateBlock(blockRequestDTO, blockDB, property));
    }

    public void delete(final Long blockId) {
        blockRepository.delete(findBlock(blockId));
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
