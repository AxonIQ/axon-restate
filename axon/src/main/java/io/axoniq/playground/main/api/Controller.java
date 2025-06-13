package io.axoniq.playground.main.api;

import io.axoniq.playground.main.core.*;
import io.axoniq.playground.main.query.BikeEntry;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/")
public class Controller {

    final private CommandGateway commandGateway;
    final private QueryGateway queryGateway;

    public Controller(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping("/setup")
    public CompletableFuture<Void> setup(@RequestParam("bikes") int bikes) {
        // register requested number of bikes in parallel.
        List<CompletableFuture<Void>> commands = new ArrayList<>();
        for (var i = 0; i < bikes; i++) {
            commands.add(
                    commandGateway.send(
                            new RegisterBikeCommand(UUID.randomUUID().toString())
                    )
            );
        }

        return CompletableFuture.allOf(commands.toArray(new CompletableFuture[0]));
    }

    @PostMapping("/requestBike")
    public CompletableFuture<Void> requestBike(
            @RequestParam("bikeId") String bikeId,
            @RequestParam("renter") String renter
    ) {
        return commandGateway.send(new RequestBikeCommand(bikeId, renter));
    }

    @PostMapping("/claimBike")
    public CompletableFuture<Void> claimBike(
            @RequestParam("bikeId") String bikeId
    ) {
        return commandGateway.send(new ClaimBikeCommand(bikeId));
    }

    @PostMapping("/returnBike")
    public CompletableFuture<Void> returnBike(
            @RequestParam("bikeId") String bikeId

    ) {
        return commandGateway.send(new ReturnBikeCommand(bikeId));
    }

    @PostMapping("/releaseBike")
    public CompletableFuture<Void> releaseBike(
            @RequestParam("bikeId") String bikeId
    ) {
        return commandGateway.send(new ReleaseBikeCommand(bikeId));
    }

    @GetMapping("/bikes")
    public CompletableFuture<List<BikeEntry>> getBikes() {
        return queryGateway.query(
                "getBikes",
                null,
                ResponseTypes.multipleInstancesOf(BikeEntry.class)
        );
    }
}
