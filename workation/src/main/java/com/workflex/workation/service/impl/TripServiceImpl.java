package com.workflex.workation.service.impl;

import com.workflex.workation.model.Trip;
import com.workflex.workation.repository.TripRepository;
import com.workflex.workation.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public void bookTrip(Trip trip) {
        tripRepository.save(trip);
    }
}
