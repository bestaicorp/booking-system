package com.booking.system.service;

import com.booking.system.dto.GuestDTO;
import com.booking.system.exception.GuestNotFoundException;
import com.booking.system.model.Guest;
import com.booking.system.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestDTO create(GuestDTO guestDTO) {

        return GuestDTO.of(guestRepository.save(GuestDTO.toGuest(guestDTO)));
    }

    public GuestDTO update(GuestDTO guestDTO) {
        Guest guestDB = guestRepository.findById(guestDTO.getId())
                .orElseThrow(() -> new GuestNotFoundException(guestDTO.getId()));
        guestDB.setName(guestDTO.getName());
        guestDB.setEmail(guestDTO.getEmail());
        return GuestDTO.of(guestDB);
    }

    public void delete(final Long guestId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
        guestRepository.delete(guest);
    }
}
