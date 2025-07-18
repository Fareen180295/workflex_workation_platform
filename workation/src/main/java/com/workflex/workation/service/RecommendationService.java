package com.workflex.workation.service;

import com.workflex.workation.model.Trip;
import com.workflex.workation.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {

    @Autowired
    public TripRepository tripRepository;

    public List<String> getRecommendations(String inputDestination) {
        List<Trip> allTrips = tripRepository.findAll();

        // Map: user -> set of destinations
        Map<String, Set<String>> userToDestinations = new HashMap<>();
        for (Trip trip : allTrips) {
            userToDestinations
                    .computeIfAbsent(trip.getUserId(), k -> new HashSet<>())
                    .add(trip.getDestination());
        }

        // Map: destination -> user vector
        Map<String, Map<String, Integer>> destinationVectors = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : userToDestinations.entrySet()) {
            String user = entry.getKey();
            for (String destination : entry.getValue()) {
                destinationVectors
                        .computeIfAbsent(destination, k -> new HashMap<>())
                        .put(user, 1);
            }
        }

        Map<String, Integer> inputVector = destinationVectors.get(inputDestination);
        if (inputVector == null) return List.of();

        return destinationVectors.entrySet().stream()
                .filter(e -> !e.getKey().equals(inputDestination))
                .map(e -> Map.entry(e.getKey(), cosineSimilarity(inputVector, e.getValue())))
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private double cosineSimilarity(Map<String, Integer> vecA, Map<String, Integer> vecB) {
        Set<String> allUsers = new HashSet<>(vecA.keySet());
        allUsers.addAll(vecB.keySet());

        double dotProduct = 0, normA = 0, normB = 0;

        for (String user : allUsers) {
            int a = vecA.getOrDefault(user, 0);
            int b = vecB.getOrDefault(user, 0);
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        return (normA == 0 || normB == 0) ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
