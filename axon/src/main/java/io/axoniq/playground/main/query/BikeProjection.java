package io.axoniq.playground.main.query;

import io.axoniq.playground.main.core.*;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// ...

@Component
@ProcessingGroup("bike-projection")
public class BikeProjection {

    private final WebClient webClient;

    @Value("${restate-ingress.url}")
    private String restateUrl;

    // projection: Bike status.
    private Map<String, BikeStatus> bikes = new HashMap<>();

    // Maps a bike to its current rental (workflow) identifier.
    private Map<String, String> rentals = new HashMap<>();

    public BikeProjection(WebClient webClient) {
        this.webClient = webClient;
    }

    @EventHandler
    public void handle(
            BikeRegisteredEvent evt
    ) {
        bikes.put(evt.bikeId(), BikeStatus.AVAILABLE);
    }

    @EventHandler
    public void handle(
            BikeRequestedEvent evt
    ) {
        bikes.put(evt.bikeId(), BikeStatus.REQUESTED);

        // invoke the rental workflow with an event payload.
        var rentalId = rentals.computeIfAbsent(evt.bikeId(), ignored -> UUID.randomUUID().toString());
        var jsonBody = String.format("{\"bikeId\":\"%s\"}", evt.bikeId());

        webClient
                .post()
                .uri(restateUrl + "/bike-rental/" + rentalId + "/run/send")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        // note: If this event handler, for the current event, is re-executed due to a failure,
        // Restate ensures that the workflow (with the same workflow id) is not run again.
    }

    @EventHandler
    public void handle(
            BikeClaimedEvent evt
    ) {
        bikes.put(evt.bikeId(), BikeStatus.RENTED);
    }

    @EventHandler
    public void handle(
            BikeReturnedEvent evt
    ) {
        bikes.put(evt.bikeId(), BikeStatus.RETURNED);

        // invoke the workflow with an event payload.
        var payload = String.format(
                "{\"status\":\"%s\"}",
                "returned"
        );

        webClient
                .post()
                .uri(restateUrl + "/bike-rental/" + rentals.get(evt.bikeId()) + "/bikeEvent/send")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @EventHandler
    public void handle(
            BikeReleasedEvent evt
    ) {
        bikes.put(evt.bikeId(), BikeStatus.AVAILABLE);

        // expire the rental workflow id for the bike.
        rentals.remove(evt.bikeId());
    }

    // ...

    @QueryHandler(queryName = "getBikes")
    public List<BikeEntry> getBikes() {
        return bikes.entrySet().stream()
                .map(e -> new BikeEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
