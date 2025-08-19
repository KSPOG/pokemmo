package plugins.walker;

import plugins.Plugin;
import javax.swing.Timer;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WalkerPlugin implements Plugin {
    private final Properties hotkeys = new Properties();
    private Timer timer;
    private Robot robot;
    private int stepIndex = 0;
    private int[] sequence;

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

    private int resolveKey(String name) {
        if (name == null) {
            return -1;
        }
        try {
            return KeyEvent.class.getField("VK_" + name.toUpperCase()).getInt(null);
        } catch (Exception e) {
            if (name.length() == 1) {
                return KeyEvent.getExtendedKeyCodeForChar(name.toUpperCase().charAt(0));
            }
        }
        return -1;
    }

    @Override
    public void start() {
        if (timer != null) {
            return;
        }
        sequence = new int[]{
                resolveKey(hotkeys.getProperty("up")),
                resolveKey(hotkeys.getProperty("right")),
                resolveKey(hotkeys.getProperty("down")),
                resolveKey(hotkeys.getProperty("left"))
        };
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        }
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int code = sequence[stepIndex];
                stepIndex = (stepIndex + 1) % sequence.length;
                if (code != -1) {
                    robot.keyPress(code);
                    robot.keyRelease(code);
                }
            }
        });
        timer.start();
        System.out.println("Walker plugin started");
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        robot = null;
        stepIndex = 0;
        System.out.println("Walker plugin stopped");
    }
}
