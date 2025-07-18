package com.workflex.workation.controller;

import com.workflex.workation.model.Trip;
import com.workflex.workation.repository.TripRepository;
import com.workflex.workation.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflex")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/trip")
    public ResponseEntity<String> bookTrip(@Valid @RequestBody Trip trip) {
        tripRepository.save(trip);
        return ResponseEntity.ok("Trip booked successfully");
    }

    @GetMapping("/destination/recommendations")
    public List<String> getRecommendations(@RequestParam String country) {
        return recommendationService.getRecommendations(country);
    }
}
