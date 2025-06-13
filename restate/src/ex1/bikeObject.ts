import * as restate from "@restatedev/restate-sdk";

import {
    BikeAggregate,
    BikeEvent,
    apply
} from './model';

import {
    BikeService
} from './bikeService';

// ...
// Bike Aggregate implemented as a Virtual Object.
export const bikeObject = restate.object({
    name: 'BikeObject',
    handlers: {
        registerBike: 
            async (ctx: restate.ObjectContext) => {
                let aggregate = await ctx.get<BikeAggregate>('aggregate');
                if (aggregate) {
                    // bike has already been registered.
                    return;
                }

                // initialize aggregate state and event stream.
                aggregate = {
                    state:  {},
                    events: []
                };

                apply(
                    {
                        type: 'BikeRegistered'
                    },
                    aggregate
                );

                ctx.set('aggregate', aggregate);
                
                return `Bike ${ctx.key} has been registered`;
            },

        requestBike: 
            async (ctx: restate.ObjectContext, req: { renter: string }) => {
                const aggregate = await ctx.get<BikeAggregate>('aggregate');
                if (!aggregate) {
                    // bike has not been registered.
                    return;
                }

                if (!aggregate.state['available']) {
                    return `Bike ${ctx.key} is not available`;
                }

                apply(
                    {
                        type: 'BikeRequested',
                        renter: req.renter
                    },
                    aggregate
                );

                ctx.set('aggregate', aggregate);

                // invoke the bike operations context to unlock the bike.
                ctx.serviceSendClient(BikeService).unlock(ctx.key);

                return `Bike ${ctx.key} has been requested by ${aggregate.state['renter']}`;
            },  

        returnBike: 
            async (ctx: restate.ObjectContext) => {
                const aggregate = await ctx.get<BikeAggregate>('aggregate');
                if (!aggregate) {
                    // bike has not been registered.
                    return;
                }

                if (aggregate.state['available']) {
                    return `Bike ${ctx.key} has not been rented`;
                }

                apply(
                    {
                        type: 'BikeReturned'
                    },
                    aggregate
                );

                ctx.set('aggregate', aggregate);

                // trigger the automation to lock the bike.
                ctx.serviceSendClient(BikeService).lock(ctx.key);

                return `Bike ${ctx.key} has been returned`;
            },

        update:
            async (ctx: restate.ObjectContext, reason: string) => {
                const aggregate = await ctx.get<BikeAggregate>('aggregate');
                if (!aggregate) {
                    // bike has not been registered.
                    return;
                }

                let event: BikeEvent;
                switch (reason) {
                    case 'bike_locked':
                        event = {
                            type: 'BikeReleased'
                        }
                        break;

                    case 'bike_unlocked':
                        event = {
                            type: 'BikeRented'
                        }
                        break;

                    default:
                        throw new Error('Reason not recognized');
                }

                apply(event, aggregate);                
                ctx.set('aggregate', aggregate);
            },

        getStatus: 
            restate.handlers.object.shared(
                async (ctx: restate.ObjectSharedContext) => {
                    const aggregate = await ctx.get<BikeAggregate>('aggregate');
                    if (!aggregate) {
                        // bike has not been registered.
                        return;
                    }

                    let status;
                    aggregate.events.forEach(event => {
                        if (event.type === 'BikeRegistered' || event.type === 'BikeReleased') {
                            status = 'available';
                        } else if (event.type === 'BikeRequested') {
                            status = 'requested';
                        } else if (event.type === 'BikeRented') {
                            status = 'rented';
                        } else {
                            status = 'returned';
                        }
                    });

                    return status;
                }
        ),
    }
});

export const BikeObject: typeof bikeObject = { name: 'BikeObject' };