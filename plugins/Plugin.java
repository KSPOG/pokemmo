package plugins;

public interface Plugin {
    String getName();

    void start();

    void stop();
}
