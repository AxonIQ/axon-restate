import axios from 'axios';

// ...
// REST endpoint for sending commands to Axon Server.
//
// Note: This assumes an unsecured node running locally.
const AXON_SERVER_URL = 'http://localhost:8024/v2/commands?context=default';

// ...
// Commands (specific to externalized bike rental application).
export type CommandType = 
    | 'ClaimBike' 
    | 'ReleaseBike';

const commandPrefix = 'io.axoniq.playground.main.core';

// ...

export const sendCommand = async (ct: CommandType, payload: unknown) => {
    await axios.post(
        AXON_SERVER_URL,
        {
            payload,
            name: `${commandPrefix}.${ct}Command`
        }
    );

    return;
};