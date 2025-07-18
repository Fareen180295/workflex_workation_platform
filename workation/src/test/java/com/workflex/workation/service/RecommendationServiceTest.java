package com.workflex.workation.service;

import com.workflex.workation.model.Trip;
import com.workflex.workation.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private RecommendationService recommendationService;
    private TripRepository tripRepository;

    @BeforeEach
    void setup() {
        tripRepository = Mockito.mock(TripRepository.class);
        recommendationService = new RecommendationService();
        recommendationService.tripRepository = tripRepository;
    }

    @Test
    void testGetRecommendations() {
        List<Trip> trips = Arrays.asList(
                new Trip("user1", "Spain"),
                new Trip("user1", "France"),
                new Trip("user2", "Spain"),
                new Trip("user3", "Spain"),
                new Trip("user3", "France"),
                new Trip("user3", "Italy")
        );

        when(tripRepository.findAll()).thenReturn(trips);

        List<String> recommendations = recommendationService.getRecommendations("Spain");

        assertFalse(recommendations.contains("Spain")); // should not recommend same destination
        assertTrue(recommendations.contains("France"));
        assertTrue(recommendations.contains("Italy"));
    }

    @Test
    void testGetRecommendationsNoData() {
        when(tripRepository.findAll()).thenReturn(List.of());

        List<String> recommendations = recommendationService.getRecommendations("Spain");
        assertTrue(recommendations.isEmpty());
    }
}
