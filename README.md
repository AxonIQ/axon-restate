# CQRS and Event Sourcing with Durable Execution
This repository demonstrates how to implement event-sourced applications in the context of durable execution using [Restate](https://restate.dev) and the [Axon](https://axoniq.io) technology stack.

## Installation of Restate and Axon Server
To install the required dependencies, follow the respective guides:

*   [Restate Quickstart](https://docs.restate.dev/get_started/quickstart)

*   [Axon Server](https://docs.axoniq.io/axon-server-installation/developer/download/)

    > Note: After following the Axon Server installation instructions, in the `axonserver.properties` file add the following:
    >
    >    axoniq.axonserver.devmode.enabled=true

## Overview
The code samples include:

*   **Aggregates with Virtual Objects**

    `restate/src/ex1` illustrates how to implement an event-sourced aggregate, where its event stream (as well as its current state that is hydrated from said event stream) is stored as K/V state in a [Restate Virtual Object](https://docs.restate.dev/concepts/services#virtual-objects). Commands and queries are sent to - and handled by - corresponding VO handlers.

*   **Utilizing an external event store and building Workflows**

    `restate/src/ex2` illustrates how to build a [Restate Workflow](https://docs.restate.dev/concepts/services#workflows) (for renting a bike) that is triggered by - and interacts with - a standalone application implemented with Axon Framework, with events stored in Axon Server.

    The Axon application is found in the `axon` directory.

### Restate
To enable a Restate code sample, uncomment the corresponding line in `restate/src/app.ts`.

> Ensure that the Restate server is running before starting the Restate code samples.

To run a Restate code sample:

*   Start it:

        npm run app-dev

*   Register its Restate services.

    E.g. to register the Virtual Object in `restate/src/ex1`:

        restate dp add http://localhost:9080

*   Deregister its Restate services.

    E.g. to deregister the Virtual Object in `restate/src/ex1`:

        restate dp remove [Virtual Object DEPLOYMENT_ID]

### Axon
Build and run the Axon Framework application (in your IDE or with Maven).

> Ensure that Axon Server is set up and running before starting the Axon Framework application.

## Samples
Overview of the code samples.

> The `restate/src/ex1` and `restate/src/ex2` samples demonstrate two approaches to implementing the event model depicted in _em_bikerental.pdf_. 

### Aggregates with Virtual Objects
A Restate Virtual Object encapsulates a domain model (command-side), read models, and orchestrates a business flow.

After starting the example application:

*   Register the Restate services _BikeObject_ and _BikeService_, running locally on ports _9080_ and _9081_ respectively.

*   Open the Restate UI and in the playground execute the bike rental flow by invoking the following handlers:

    *   `registerBike`
    
        Register a bike.
    
    *   `requestBike`
    
        Request a bike. 
        
        Pass along a JSON body of 
        
            { "renter": "NameOfRenter" }.

    *   `returnBike`
    
        Return a bike.

*   Deregister the Restate services.

### Utilizing an external event store and building Workflows
After starting both the Restate and Axon applications:

*   Register the Restate services _BikeWorkflow_ and _BikeService_, running locally on ports _9080_ and _9081_ respectively.

*   Send the following HTTP requests to the Axon application (running locally on port _8888_):

    *   `POST http://localhost:8888/setup?bikes=1`

        Register a bike.

    *   `GET http://localhost:8888/bikes`

        Get the bike identifier.

    *   `POST http://localhost:8888/requestBike?bikeId=<bikeId>&renter=<nameOfRenter>`

        Request a bike with its identifier and name of renter.

    *   `POST http://localhost:8888/returnBike?bikeId=<bikeId>`

        Return a bike with its identifier.

    > To inspect the workflow, after requesting the bike open the Restate UI and navigate to _Invocations_; the bike rental workflow will be running and currently awaiting the bike's return.

*   Deregister the Restate services.