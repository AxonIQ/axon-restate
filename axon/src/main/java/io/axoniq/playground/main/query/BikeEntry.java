package io.axoniq.playground.main.query;

enum BikeStatus {
    AVAILABLE,
    REQUESTED,
    RENTED,
    RETURNED
}

public record BikeEntry(String bikeId, BikeStatus status) {}
