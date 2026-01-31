package com.booking.system.service;

import com.booking.system.dto.PropertyRequestDTO;
import com.booking.system.dto.PropertyResponseDTO;
import com.booking.system.exception.PropertyNotFoundException;
import com.booking.system.model.Property;
import com.booking.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyResponseDTO create(PropertyRequestDTO propertyRequestDTO) {
        log.info("Creating property with name: {}", propertyRequestDTO.getName());
        Property saved = propertyRepository.save(PropertyRequestDTO.toProperty(propertyRequestDTO));
        log.info("Property created successfully with id {}", saved.getId());
        return PropertyResponseDTO.of(saved);
    }

    public Page<PropertyResponseDTO> getAll(int page, int size) {
        log.debug("Fetching properties page {} with size {}", page, size);
        return propertyRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(PropertyResponseDTO::of);
    }

    public PropertyResponseDTO get(Long propertyId) {
        log.debug("Fetching property with id {}", propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));
        return PropertyResponseDTO.of(property);
    }

    public PropertyResponseDTO update(PropertyRequestDTO propertyRequestDTO, Long id) {
        log.info("Updating property {}", id);
        Property propertyDB = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException(id));
        propertyDB.setName(propertyRequestDTO.getName());
        propertyDB.setType(propertyRequestDTO.getType());
        log.info("Property {} updated successfully", id);
        return PropertyResponseDTO.of(propertyDB);
    }

    public void delete(Long propertyId) {
        log.info("Deleting property {}", propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));
        propertyRepository.delete(property);
        log.info("Property {} deleted successfully", propertyId);
    }
}
