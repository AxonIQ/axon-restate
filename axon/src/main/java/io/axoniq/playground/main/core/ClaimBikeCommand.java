package io.axoniq.playground.main.core;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ClaimBikeCommand(
        @TargetAggregateIdentifier String bikeId
) {
}