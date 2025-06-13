package io.axoniq.playground.main.core;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ReleaseBikeCommand(
        @TargetAggregateIdentifier String bikeId
) {
}
