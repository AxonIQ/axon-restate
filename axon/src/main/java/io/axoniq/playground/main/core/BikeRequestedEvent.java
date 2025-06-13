package io.axoniq.playground.main.core;

public record BikeRequestedEvent(
        String bikeId,
        String renter
) {
}
