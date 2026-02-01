package com.booking.system.controller;

import com.booking.system.dto.BookingRequestDTO;
import com.booking.system.dto.GuestRequestDTO;
import com.booking.system.dto.PropertyRequestDTO;
import com.booking.system.enumeration.PropertyType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class PropertyGuestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== PROPERTY ====================

    @Test
    void createProperty_returns201() throws Exception {
        PropertyRequestDTO dto = new PropertyRequestDTO();
        dto.setName("Beach House");
        dto.setType(PropertyType.HOUSE);

        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Beach House"))
                .andExpect(jsonPath("$.type").value("HOUSE"));
    }

    @Test
    void getProperty_existingId_returns200() throws Exception {
        Long id = createProperty("Beach House");

        mockMvc.perform(get("/api/v1/properties/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Beach House"));
    }

    @Test
    void getProperty_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/properties/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProperty_returns200() throws Exception {
        Long id = createProperty("Beach House");

        PropertyRequestDTO updateDTO = new PropertyRequestDTO();
        updateDTO.setName("Mountain Villa");
        updateDTO.setType(PropertyType.VILLA);

        mockMvc.perform(put("/api/v1/properties/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mountain Villa"))
                .andExpect(jsonPath("$.type").value("VILLA"));
    }

    @Test
    void deleteProperty_returns204() throws Exception {
        Long id = createProperty("Beach House");

        mockMvc.perform(delete("/api/v1/properties/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/properties/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ==================== PROPERTY — VALIDATION ====================

    @Test
    void createProperty_blankName_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"type\":\"HOUSE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProperty_nullType_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Beach House\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProperty_invalidType_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Beach House\",\"type\":\"SPACESHIP\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProperty_nonExisting_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/properties/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== GUEST ====================

    @Test
    void createGuest_returns201() throws Exception {
        GuestRequestDTO dto = new GuestRequestDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");

        mockMvc.perform(post("/api/v1/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getGuest_existingId_returns200() throws Exception {
        Long id = createGuest("John Doe", "john@example.com");

        mockMvc.perform(get("/api/v1/guests/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getGuest_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/guests/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGuest_returns200() throws Exception {
        Long id = createGuest("John Doe", "john@example.com");

        GuestRequestDTO updateDTO = new GuestRequestDTO();
        updateDTO.setName("Jane Doe");
        updateDTO.setEmail("jane@example.com");

        mockMvc.perform(put("/api/v1/guests/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void deleteGuest_returns204() throws Exception {
        Long id = createGuest("John Doe", "john@example.com");

        mockMvc.perform(delete("/api/v1/guests/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/guests/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ==================== GUEST — VALIDATION ====================

    @Test
    void createGuest_blankName_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGuest_invalidEmail_returns400() throws Exception {
        GuestRequestDTO dto = new GuestRequestDTO();
        dto.setName("John Doe");
        dto.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGuest_blankEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ==================== CASCADE PROTECTION ====================

    @Test
    void deleteProperty_withActiveBooking_returns409() throws Exception {
        Long propId = createProperty("Beach House");
        Long guestId = createGuest("John Doe", "john@example.com");
        createBooking(propId, guestId, 1, 5);

        mockMvc.perform(delete("/api/v1/properties/{id}", propId))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteGuest_withActiveBooking_returns409() throws Exception {
        Long propId = createProperty("Beach House");
        Long guestId = createGuest("John Doe", "john@example.com");
        createBooking(propId, guestId, 1, 5);

        mockMvc.perform(delete("/api/v1/guests/{id}", guestId))
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

    private void createBooking(Long propertyId, Long guestId, int startDay, int endDay) throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setPropertyId(propertyId);
        dto.setGuestId(guestId);
        dto.setStartDate(LocalDate.now().plusDays(startDay));
        dto.setEndDate(LocalDate.now().plusDays(endDay));
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
