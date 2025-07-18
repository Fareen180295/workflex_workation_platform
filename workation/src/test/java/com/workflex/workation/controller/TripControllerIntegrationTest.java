package com.workflex.workation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflex.workation.model.Trip;
import com.workflex.workation.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class TripControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBookTripAndGetRecommendations() throws Exception {
        Trip trip = new Trip("integrationUser", "Germany");

        // Book a trip
        mockMvc.perform(post("/workflex/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isOk())
                .andExpect(content().string("Trip booked successfully"));

        // Add some other trips for recommendations
        tripRepository.save(new Trip("integrationUser", "France"));
        tripRepository.save(new Trip("user2", "Germany"));
        tripRepository.save(new Trip("user2", "France"));

        // Get recommendations for Germany
        mockMvc.perform(get("/workflex/destination/recommendations")
                        .param("country", "Germany"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
