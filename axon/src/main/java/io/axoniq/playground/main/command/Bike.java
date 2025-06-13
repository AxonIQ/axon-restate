package io.axoniq.playground.main.command;

import io.axoniq.playground.main.core.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;

    private String renter;

    // ...

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(RegisterBikeCommand cmd) {
        apply(new BikeRegisteredEvent(cmd.bikeId()));
    }

    @CommandHandler
    public void handle(RequestBikeCommand cmd) {
        apply(new BikeRequestedEvent(bikeId, cmd.renter()));
    }

    @CommandHandler
    public void handle(ClaimBikeCommand cmd) {
        apply(new BikeClaimedEvent(bikeId));
    }

    @CommandHandler
    public void handle(ReturnBikeCommand cmd) {
        apply(new BikeReturnedEvent(bikeId));
    }

    @CommandHandler
    public void handle(ReleaseBikeCommand cmd) {
        apply(new BikeReleasedEvent(bikeId));
    }

    // ...

    @EventSourcingHandler
    public void handle(BikeRegisteredEvent evt) {
        bikeId = evt.bikeId();
    }

    @EventSourcingHandler
    public void handle(BikeRequestedEvent evt) {
        renter = evt.renter();
    }

    @EventSourcingHandler
    public void handle(BikeReleasedEvent evt) {
        renter = null;
    }
}
