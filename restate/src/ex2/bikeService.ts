import * as restate from "@restatedev/restate-sdk";

// ...

export const bikeService = restate.service({
    name: 'BikeService',
    handlers: {
        lock: 
            async (ctx: restate.Context, bikeId: string) => {
                // simulate an OTA call to unlock a bike.
                await ctx.sleep(2 * 1000);

                return 'SUCCESS';
            },

        unlock: 
            async (ctx: restate.Context, bikeId: string) => {
                // simulate an OTA call to lock a bike.
                await ctx.sleep(2 * 1000);

                return 'SUCCESS';
            }
    }
});

export const BikeService: typeof bikeService = { name: 'BikeService' };