package com.workflex.workation.loader;

import com.opencsv.CSVReader;
import com.workflex.workation.model.Trip;
import com.workflex.workation.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TripRepository tripRepository;

    @Override
    public void run(String... args) throws Exception {
        try (
                Reader reader = new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("trip-tracking.csv")
                );
                CSVReader csvReader = new CSVReader(reader)
        ) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length >= 2) {
                    Trip trip = new Trip(line[0].trim(), line[1].trim());
                    tripRepository.save(trip);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading CSV data: " + e.getMessage());
        }
    }
}
