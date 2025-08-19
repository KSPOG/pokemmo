public class SamplePlugin implements ClientPlugin {
    @Override
    public void run(Client client) {
        client.getLogger().info("SamplePlugin executed");
    }
}
