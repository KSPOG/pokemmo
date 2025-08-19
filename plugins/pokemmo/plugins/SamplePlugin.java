package pokemmo.plugins;

import pokemmo.Client;
import pokemmo.ClientPlugin;

/**
 * Simple demonstration plugin that logs when run.
 */
public class SamplePlugin implements ClientPlugin {
    @Override
    public void run(Client client) {
        // Log through the client's logger to verify plugin execution
        client.getLogger().info("SamplePlugin executed");
    }
}
