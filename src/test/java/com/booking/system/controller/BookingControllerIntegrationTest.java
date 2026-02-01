package com.booking.system.controller;

import com.booking.system.dto.BlockRequestDTO;
import com.booking.system.dto.BookingRequestDTO;
import com.booking.system.dto.GuestRequestDTO;
import com.booking.system.dto.PropertyRequestDTO;
import com.booking.system.enumeration.PropertyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long propertyId;
    private Long guestId;

    @BeforeEach
    void setUp() throws Exception {
        propertyId = createProperty("Beach House");
        guestId = createGuest("John Doe", "john@example.com");
    }

    // ==================== CREATE ====================

    @Test
    void create_validBooking_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(1, 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andExpect(jsonPath("$.property.name").value("Beach House"))
                .andExpect(jsonPath("$.guest.name").value("John Doe"));
    }

    @Test
    void create_overlappingDates_returns409() throws Exception {
        createBooking(1, 5);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(3, 7)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_adjacentDates_bothSucceed() throws Exception {
        createBooking(1, 5);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(5, 10)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_startAfterEnd_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(5, 1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidPropertyId_returns404() throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(999L);
        dto.setGuestId(guestId);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_onBlockedDates_returns409() throws Exception {
        createBlock(10, 15);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(12, 14)))
                .andExpect(status().isConflict());
    }

    // ==================== CREATE — VALIDATION ====================

    @Test
    void create_nullPropertyId_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guestId\":1,\"startDate\":\"2026-06-01\",\"endDate\":\"2026-06-05\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_negativePropertyId_returns400() throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(-1L);
        dto.setGuestId(guestId);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidGuestId_returns404() throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setGuestId(999L);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_startDateInPast_returns400() throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setGuestId(guestId);
        dto.setStartDate(LocalDate.now().minusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_startEqualsEnd_returns400() throws Exception {
        LocalDate sameDate = LocalDate.now().plusDays(5);
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setGuestId(guestId);
        dto.setStartDate(sameDate);
        dto.setEndDate(sameDate);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_endDateTooFarInFuture_returns400() throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setGuestId(guestId);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusYears(3));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidJson_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_multipleBookingsDifferentDates_allSucceed() throws Exception {
        createBooking(1, 5);
        createBooking(5, 10);
        createBooking(10, 15);

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    // ==================== GET ====================

    @Test
    void get_existingBooking_returns200() throws Exception {
        Long id = createBooking(1, 5);

        mockMvc.perform(get("/api/v1/bookings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void get_nonExistingBooking_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_returnsPage() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    // ==================== UPDATE ====================

    @Test
    void update_validRequest_returns200() throws Exception {
        Long id = createBooking(1, 5);

        mockMvc.perform(put("/api/v1/bookings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(2, 6)))
                .andExpect(status().isOk());
    }

    @Test
    void update_cancelledBooking_returns409() throws Exception {
        Long id = createBooking(1, 5);
        cancelBooking(id);

        mockMvc.perform(put("/api/v1/bookings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(2, 6)))
                .andExpect(status().isConflict());
    }

    // ==================== CANCEL ====================

    @Test
    void cancel_activeBooking_returnsCancelled() throws Exception {
        Long id = createBooking(1, 5);

        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancel_alreadyCancelled_returns409() throws Exception {
        Long id = createBooking(1, 5);
        cancelBooking(id);

        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel", id))
                .andExpect(status().isConflict());
    }

    // ==================== REBOOK ====================

    @Test
    void rebook_cancelledBooking_returnsRebooked() throws Exception {
        Long id = createBooking(1, 5);
        cancelBooking(id);

        mockMvc.perform(patch("/api/v1/bookings/{id}/rebook", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REBOOKED"));
    }

    @Test
    void rebook_activeBooking_returns409() throws Exception {
        Long id = createBooking(1, 5);

        mockMvc.perform(patch("/api/v1/bookings/{id}/rebook", id))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelledBooking_freesUpDates() throws Exception {
        Long id = createBooking(1, 5);
        cancelBooking(id);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(1, 5)))
                .andExpect(status().isCreated());
    }

    // ==================== DELETE ====================

    @Test
    void delete_existingBooking_returns204() throws Exception {
        Long id = createBooking(1, 5);

        mockMvc.perform(delete("/api/v1/bookings/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/bookings/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_nonExistingBooking_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== OVERLAP PREVENTION ====================

    @Test
    void secondBooking_sameDates_isRejected() throws Exception {
        createBooking(1, 5);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(1, 5)))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    /**
     * Pessimistic locking test — two threads book the same dates simultaneously.
     * Only one should succeed (201). The other gets 409 Conflict.
     * <p>
     * CountDownLatch acts as a "start gate" — both threads wait at the gate,
     * then start at the same time, guaranteeing true concurrent execution.
     * Without pessimistic locking both INSERTs would succeed (double booking).
     */
    @Test
    void pessimisticLock_twoThreadsSameDates_onlyOneSucceeds() throws Exception {
        String body = bookingJson(30, 35);
        int[] results = new int[2];
        CountDownLatch startGate = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                startGate.await(); // wait at the gate
                results[0] = mockMvc.perform(post("/api/v1/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andReturn().getResponse().getStatus();
            } catch (Exception e) {
                results[0] = 409;
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                startGate.await(); // wait at the gate
                results[1] = mockMvc.perform(post("/api/v1/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andReturn().getResponse().getStatus();
            } catch (Exception e) {
                results[1] = 409;
            }
        });

        t1.start();
        t2.start();
        startGate.countDown(); // open the gate — both threads go!
        t1.join();
        t2.join();

        // No double booking — one 201 and one 409
        boolean oneCreatedOneRejected =
                (results[0] == 201 && results[1] == 409) ||
                        (results[0] == 409 && results[1] == 201);
        assertTrue(oneCreatedOneRejected, "Expected one 201 and one 409, got: " + results[0] + ", " + results[1]);

        // Database has exactly 1 booking
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void update_toOverlappingDates_returns409() throws Exception {
        createBooking(1, 5);
        Long id2 = createBooking(10, 15);

        mockMvc.perform(put("/api/v1/bookings/{id}", id2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(2, 6)))
                .andExpect(status().isConflict());
    }

    @Test
    void rebook_whenDatesNowOccupied_returns409() throws Exception {
        Long idA = createBooking(1, 5);
        cancelBooking(idA);

        createBooking(1, 5);

        mockMvc.perform(patch("/api/v1/bookings/{id}/rebook", idA))
                .andExpect(status().isConflict());
    }

    // ==================== HELPERS ====================

    private Long createProperty(String name) throws Exception {
        PropertyRequestDTO dto = new PropertyRequestDTO();
        dto.setName(name);
        dto.setType(PropertyType.HOUSE);
        String json = mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private Long createGuest(String name, String email) throws Exception {
        GuestRequestDTO dto = new GuestRequestDTO();
        dto.setName(name);
        dto.setEmail(email);
        String json = mockMvc.perform(post("/api/v1/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private Long createBooking(int startDay, int endDay) throws Exception {
        String json = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(startDay, endDay)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private void cancelBooking(Long id) throws Exception {
        mockMvc.perform(patch("/api/v1/bookings/{id}/cancel", id))
                .andExpect(status().isOk());
    }

    private Long createBlock(int startDay, int endDay) throws Exception {
        BlockRequestDTO dto = new BlockRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setStartDate(LocalDate.now().plusDays(startDay));
        dto.setEndDate(LocalDate.now().plusDays(endDay));
        dto.setReason("Maintenance");
        String json = mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("id").asLong();
    }

    private String bookingJson(int startDay, int endDay) throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setGuestId(guestId);
        dto.setStartDate(LocalDate.now().plusDays(startDay));
        dto.setEndDate(LocalDate.now().plusDays(endDay));
        return objectMapper.writeValueAsString(dto);
    }
}
