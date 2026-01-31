package com.booking.system.service;

import com.booking.system.dto.GuestRequestDTO;
import com.booking.system.dto.GuestResponseDTO;
import com.booking.system.exception.GuestNotFoundException;
import com.booking.system.model.Guest;
import com.booking.system.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestResponseDTO create(GuestRequestDTO guestRequestDTO) {
        log.info("Creating guest with email: {}", guestRequestDTO.getEmail());
        Guest saved = guestRepository.save(GuestRequestDTO.toGuest(guestRequestDTO));
        log.info("Guest created successfully with id {}", saved.getId());
        return GuestResponseDTO.of(saved);
    }

    public GuestResponseDTO update(GuestRequestDTO guestRequestDTO, Long id) {
        log.info("Updating guest {}", id);
        Guest guestDB = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
        guestDB.setName(guestRequestDTO.getName());
        guestDB.setEmail(guestRequestDTO.getEmail());
        log.info("Guest {} updated successfully", id);
        return GuestResponseDTO.of(guestDB);
    }

    public void delete(Long guestId) {
        log.info("Deleting guest {}", guestId);
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
        guestRepository.delete(guest);
        log.info("Guest {} deleted successfully", guestId);
    }
}
