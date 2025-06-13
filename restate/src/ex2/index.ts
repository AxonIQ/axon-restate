import * as restate from "@restatedev/restate-sdk";

import {
    bikeWorkflow
} from './bikeWorkflow';

import {
    bikeService
} from './bikeService';

// ...


restate.endpoint().bind(bikeWorkflow).listen(9080);
restate.endpoint().bind(bikeService).listen(9081);