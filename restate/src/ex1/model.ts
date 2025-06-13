type Dict = {
    [key: string]: unknown
};

// ...

export type BikeAggregate = {
    state:  Dict,
    events: BikeEvent[] 
};

type BikeRegisteredEvent = {
    type: 'BikeRegistered'
};

type BikeRequestedEvent = {
    type: 'BikeRequested',

    renter: string
};

type BikeRentedEvent = {
    type: 'BikeRented'
};

type BikeReturnedEvent = {
    type: 'BikeReturned'
};

type BikeReleasedEvent = {
    type: 'BikeReleased'
};

export type BikeEvent =
    | BikeRegisteredEvent
    | BikeRequestedEvent
    | BikeRentedEvent 
    | BikeReturnedEvent
    | BikeReleasedEvent;

// ...
// Apply a new event to update the (current) aggregate state and event stream.
export const apply = (event: BikeEvent, aggregate: BikeAggregate) => {
    switch (event.type) {
        case 'BikeRegistered':
            aggregate.state['available'] = true;   
            break;

        case 'BikeRequested':
            aggregate.state['available'] = false;
            aggregate.state['renter'] = event.renter;
            break;

        case 'BikeReleased':
            aggregate.state['available'] = true;
            aggregate.state['renter'] = null;
            break;
    }

    aggregate.events.push(event);
};