import * as restate from "@restatedev/restate-sdk";

import {
    BikeObject
} from './bikeObject';

// ...

export const bikeService = restate.service({
    name: 'BikeService',
    handlers: {
        lock: 
            async (ctx: restate.Context, bikeId: string) => {
                // simulate an OTA call to unlock a bike.
                await ctx.sleep(2 * 1000);
                ctx.objectSendClient(BikeObject, bikeId).update('bike_locked');
            },

        unlock: 
            async (ctx: restate.Context, bikeId: string) => {
                // simulate an OTA call to lock a bike.
                await ctx.sleep(2 * 1000);
                ctx.objectSendClient(BikeObject, bikeId).update('bike_unlocked');
            }
    }
});

export const BikeService: typeof bikeService = { name: 'BikeService' };