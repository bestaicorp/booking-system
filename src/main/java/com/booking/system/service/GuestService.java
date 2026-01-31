package com.booking.system.service;

import com.booking.system.dto.GuestDTO;
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

    public GuestDTO create(GuestDTO guestDTO) {
        log.info("Creating guest with email: {}", guestDTO.getEmail());
        Guest saved = guestRepository.save(GuestDTO.toGuest(guestDTO));
        log.info("Guest created successfully with id {}", saved.getId());
        return GuestDTO.of(saved);
    }

    public GuestDTO update(GuestDTO guestDTO) {
        log.info("Updating guest {}", guestDTO.getId());
        Guest guestDB = guestRepository.findById(guestDTO.getId())
                .orElseThrow(() -> new GuestNotFoundException(guestDTO.getId()));
        guestDB.setName(guestDTO.getName());
        guestDB.setEmail(guestDTO.getEmail());
        log.info("Guest {} updated successfully", guestDTO.getId());
        return GuestDTO.of(guestDB);
    }

    public void delete(final Long guestId) {
        log.info("Deleting guest {}", guestId);
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
        guestRepository.delete(guest);
        log.info("Guest {} deleted successfully", guestId);
    }
}
