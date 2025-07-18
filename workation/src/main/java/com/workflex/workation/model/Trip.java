package com.workflex.workation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User must not be blank")
    private String userId;

    @NotBlank(message = "Destination must not be blank")
    private String destination;

    public Trip() {}

    public Trip(String userId, String destination) {
        this.userId = userId;
        this.destination = destination;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDestination() {
        return destination;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
