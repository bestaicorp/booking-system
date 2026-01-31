package com.booking.system.service;

import com.booking.system.dto.PropertyDTO;
import com.booking.system.exception.PropertyNotFoundException;
import com.booking.system.model.Property;
import com.booking.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyDTO create(PropertyDTO propertyDTO) {
        log.info("Creating property with name: {}", propertyDTO.getName());
        Property saved = propertyRepository.save(PropertyDTO.toProperty(propertyDTO));
        log.info("Property created successfully with id {}", saved.getId());
        return PropertyDTO.of(saved);
    }

    public PropertyDTO update(PropertyDTO propertyDTO) {
        log.info("Updating property {}", propertyDTO.getId());
        Property propertyDB = propertyRepository.findById(propertyDTO.getId())
                .orElseThrow(() -> new PropertyNotFoundException(propertyDTO.getId()));
        propertyDB.setName(propertyDTO.getName());
        propertyDB.setType(propertyDTO.getType());
        log.info("Property {} updated successfully", propertyDTO.getId());
        return PropertyDTO.of(propertyDB);
    }

    public void delete(final Long propertyId) {
        log.info("Deleting property {}", propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));
        propertyRepository.delete(property);
        log.info("Property {} deleted successfully", propertyId);
    }
}
