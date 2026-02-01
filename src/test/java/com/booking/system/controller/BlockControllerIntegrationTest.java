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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BlockControllerIntegrationTest {

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
    void create_validBlock_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson(1, 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.reason").value("Maintenance"))
                .andExpect(jsonPath("$.property.name").value("Beach House"));
    }

    @Test
    void create_overlappingBlocks_returns409() throws Exception {
        createBlock(1, 5);

        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson(3, 7)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_adjacentBlocks_bothSucceed() throws Exception {
        createBlock(1, 5);

        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson(5, 10)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_blockOnBookedDates_returns409() throws Exception {
        createBooking(10, 15);

        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson(12, 14)))
                .andExpect(status().isConflict());
    }

    // ==================== CREATE — VALIDATION ====================

    @Test
    void create_startAfterEnd_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson(5, 1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_startEqualsEnd_returns400() throws Exception {
        BlockRequestDTO dto = new BlockRequestDTO();
        dto.setPropertyId(propertyId);
        LocalDate sameDate = LocalDate.now().plusDays(5);
        dto.setStartDate(sameDate);
        dto.setEndDate(sameDate);

        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_nullPropertyId_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2026-06-01\",\"endDate\":\"2026-06-05\",\"reason\":\"Test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidPropertyId_returns404() throws Exception {
        BlockRequestDTO dto = new BlockRequestDTO();
        dto.setPropertyId(999L);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_nonExistingBlock_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/blocks/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ====================

    @Test
    void get_existingBlock_returns200() throws Exception {
        Long id = createBlock(1, 5);

        mockMvc.perform(get("/api/v1/blocks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Maintenance"));
    }

    @Test
    void get_nonExistingBlock_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/blocks/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== UPDATE ====================

    @Test
    void update_validRequest_returns200() throws Exception {
        Long id = createBlock(1, 5);

        BlockRequestDTO updateDTO = new BlockRequestDTO();
        updateDTO.setPropertyId(propertyId);
        updateDTO.setStartDate(LocalDate.now().plusDays(2));
        updateDTO.setEndDate(LocalDate.now().plusDays(6));
        updateDTO.setReason("Renovation");

        mockMvc.perform(put("/api/v1/blocks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Renovation"));
    }

    @Test
    void update_toOverlappingDates_returns409() throws Exception {
        createBlock(1, 5);
        Long id2 = createBlock(10, 15);

        BlockRequestDTO updateDTO = new BlockRequestDTO();
        updateDTO.setPropertyId(propertyId);
        updateDTO.setStartDate(LocalDate.now().plusDays(2));
        updateDTO.setEndDate(LocalDate.now().plusDays(6));
        updateDTO.setReason("Renovation");

        mockMvc.perform(put("/api/v1/blocks/{id}", id2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isConflict());
    }

    // ==================== DELETE ====================

    @Test
    void delete_existingBlock_returns204() throws Exception {
        Long id = createBlock(1, 5);

        mockMvc.perform(delete("/api/v1/blocks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/blocks/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_blockFreesUpDatesForBooking() throws Exception {
        Long blockId = createBlock(20, 25);
        mockMvc.perform(delete("/api/v1/blocks/{id}", blockId));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(20, 25)))
                .andExpect(status().isCreated());
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

    private Long createBlock(int startDay, int endDay) throws Exception {
        String json = mockMvc.perform(post("/api/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson(startDay, endDay)))
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

    private String blockJson(int startDay, int endDay) throws Exception {
        BlockRequestDTO dto = new BlockRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setStartDate(LocalDate.now().plusDays(startDay));
        dto.setEndDate(LocalDate.now().plusDays(endDay));
        dto.setReason("Maintenance");
        return objectMapper.writeValueAsString(dto);
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
