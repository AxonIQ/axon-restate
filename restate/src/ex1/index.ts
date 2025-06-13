import * as restate from "@restatedev/restate-sdk";

import {
    bikeObject
} from './bikeObject';

import {
    bikeService
} from './bikeService';

// ...

restate.endpoint().bind(bikeObject).listen(9080);
restate.endpoint().bind(bikeService).listen(9081);