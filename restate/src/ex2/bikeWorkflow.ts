import * as restate from "@restatedev/restate-sdk";

import {
    BikeService
} from './bikeService';

import {
    sendCommand
} from './axon';

// ...

export const bikeWorkflow = restate.workflow({
    name: 'bike-rental',
    handlers: {
        run: async (ctx: restate.WorkflowContext, req: { bikeId: string }) => {
            const { 
                bikeId
            } = req;

            // invoke the bike operations context to unlock the bike.
            await ctx.serviceSendClient(BikeService).unlock(bikeId);

            // send a command to claim the bike.
            await ctx.run(() => sendCommand("ClaimBike", {
                bikeId
            }));

            // await a bike to be returned (optionally provide a timeout).
            await ctx.promise('returned');

            // invoke the bike operations context to lock the bike.
            await ctx.serviceSendClient(BikeService).lock(bikeId);

            // send a command to release the bike.
            await ctx.run(() => sendCommand("ReleaseBike", {
                bikeId
            }));

        },

        bikeEvent: async (ctx: restate.WorkflowSharedContext, event: { status: string }) => {
            await ctx.promise(event.status).resolve();
        }
    }
});