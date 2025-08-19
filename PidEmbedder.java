
import javax.swing.JFrame;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that attempts to reparent a game window into the launcher.
 * PID based embedding proved unreliable on some systems, so this helper now
 * locates the client window by its title. The implementation relies on
 * external command line tools such as {@code xdotool} on X11 systems. When
 * these tools are unavailable the method will simply log a warning and return
 * without affecting the running client.
 */
public final class PidEmbedder {
    private static final Logger LOGGER = Logger.getLogger(PidEmbedder.class.getName());

    private PidEmbedder() {
    }

    /**
     * Attempt to reparent the window matching {@code windowName} into
     * {@code host}. Currently implemented for Linux/X11 via the
     * {@code xdotool} command.
     *
     * @param windowName title of the game client window
     * @param host       host frame to embed the game window into
     */
    public static void reparent(String windowName, JFrame host) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("linux")) {
            LOGGER.warning("Window embedding is only supported on Linux with xdotool");
            return;
        }

        try {
            String hostId = queryWindowId(host.getTitle());
            if (hostId == null) {
                LOGGER.warning("Could not determine launcher window id");
                return;
            }

            String childId = queryWindowId("^" + windowName + "$");
            if (childId == null || childId.isEmpty()) {
                LOGGER.warning("No window found matching title " + windowName);
                return;
            }

            new ProcessBuilder("xdotool", "windowreparent", childId.trim(), hostId.trim()).start().waitFor();
            // Ensure the window becomes visible immediately after reparenting.
            new ProcessBuilder("xdotool", "windowmap", childId.trim()).start().waitFor();
            new ProcessBuilder("xdotool", "windowraise", childId.trim()).start().waitFor();
            LOGGER.info("Reparented game window " + childId.trim() + " into launcher and mapped");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to embed window by name", e);
        }
    }

    /**
     * Attempt to reparent the window belonging to {@code pid} into
     * {@code host}. This uses {@code xdotool search --all --pid} and
     * is only supported on Linux systems.
     *
     * @param pid  process id of the game client
     * @param host host frame to embed the game window into
     */
    public static void reparent(long pid, JFrame host) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("linux")) {
            LOGGER.warning("PID-based embedding is only supported on Linux with xdotool");
            return;
        }

        try {
            String hostId = queryWindowId(host.getTitle());
            if (hostId == null) {
                LOGGER.warning("Could not determine launcher window id");
                return;
            }

            String childId = queryWindowIdByPid(pid);
            if (childId == null || childId.isEmpty()) {
                LOGGER.warning("No window found for pid " + pid);
                return;
            }

            new ProcessBuilder("xdotool", "windowreparent", childId.trim(), hostId.trim()).start().waitFor();
            new ProcessBuilder("xdotool", "windowmap", childId.trim()).start().waitFor();
            new ProcessBuilder("xdotool", "windowraise", childId.trim()).start().waitFor();
            LOGGER.info("Reparented game window " + childId.trim() + " into launcher via pid");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to embed window by pid", e);
        }
    }

    private static String queryWindowId(String title) {
        try {
            // Do not restrict search to visible windows; the client may start hidden
            // and become visible only after reparenting. Query all windows by name.
            Process p = new ProcessBuilder("xdotool", "search", "--name", title).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String id = br.readLine();
            p.waitFor();
            return id;
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to query window id", e);
            return null;
        }
    }

    private static String queryWindowIdByPid(long pid) {
        try {
            Process p = new ProcessBuilder("xdotool", "search", "--all", "--pid", String.valueOf(pid), "").start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String id = br.readLine();
            p.waitFor();
            return id;
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to query window id by pid", e);
            return null;
        }
    }
}

