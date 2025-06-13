package io.axoniq.playground.main.core;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RequestBikeCommand(
        @TargetAggregateIdentifier String bikeId,
        String renter
) {
}
