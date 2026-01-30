package com.booking.system.service;

import com.booking.system.dto.PropertyDTO;
import com.booking.system.exception.PropertyNotFoundException;
import com.booking.system.model.Property;
import com.booking.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyDTO create(PropertyDTO propertyDTO) {
        return PropertyDTO.of(propertyRepository.save(PropertyDTO.toProperty(propertyDTO)));
    }

    public PropertyDTO update(PropertyDTO propertyDTO) {
        Property propertyDB = propertyRepository.findById(propertyDTO.getId())
                .orElseThrow(() -> new PropertyNotFoundException(propertyDTO.getId()));
        propertyDB.setName(propertyDTO.getName());
        propertyDB.setType(propertyDTO.getType());
        return PropertyDTO.of(propertyDB);
    }

    public void delete(final Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId));
        propertyRepository.delete(property);
    }
}
