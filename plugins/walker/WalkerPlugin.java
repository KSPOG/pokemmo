package plugins.walker;

import plugins.Plugin;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WalkerPlugin implements Plugin {
    private final Properties hotkeys = new Properties();

    public WalkerPlugin() {
        hotkeys.setProperty("up", "W");
        hotkeys.setProperty("down", "S");
        hotkeys.setProperty("left", "A");
        hotkeys.setProperty("right", "D");
        try (InputStream in = WalkerPlugin.class.getResourceAsStream("walker.properties")) {
            if (in != null) {
                hotkeys.load(in);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public String getName() {
        return "Walker";
    }

    public String getHotkey(String direction) {
        return hotkeys.getProperty(direction);
    }
}
