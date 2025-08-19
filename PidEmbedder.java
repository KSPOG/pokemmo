
import javax.swing.JFrame;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that attempts to reparent a game window into the launcher
 * using the process ID of the spawned client. The implementation relies on
 * external command line tools such as {@code xdotool} on X11 systems. When
 * these tools are unavailable the method will simply log a warning and return
 * without affecting the running client.
 */
public final class PidEmbedder {
    private static final Logger LOGGER = Logger.getLogger(PidEmbedder.class.getName());

    private PidEmbedder() {
    }

    /**
     * Attempt to reparent the window owned by {@code pid} into {@code host}.
     * Currently implemented for Linux/X11 via the {@code xdotool} command.
     *
     * @param pid  process identifier of the game client
     * @param host host frame to embed the game window into
     */
    public static void reparent(long pid, JFrame host) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("linux")) {
            LOGGER.warning("PID based embedding is only supported on Linux with xdotool");
            return;
        }

        try {
            String hostId = queryWindowId(host.getTitle());
            if (hostId == null) {
                LOGGER.warning("Could not determine launcher window id");
                return;
            }

            Process search = new ProcessBuilder("xdotool", "search", "--pid", Long.toString(pid)).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(search.getInputStream()));
            String childId = br.readLine();
            search.waitFor();
            if (childId == null || childId.isEmpty()) {
                LOGGER.warning("No window found for pid " + pid);
                return;
            }

            new ProcessBuilder("xdotool", "windowreparent", childId.trim(), hostId.trim()).start().waitFor();
            // Ensure the window becomes visible immediately after reparenting.
            new ProcessBuilder("xdotool", "windowmap", childId.trim()).start().waitFor();
            new ProcessBuilder("xdotool", "windowraise", childId.trim()).start().waitFor();
            LOGGER.info("Reparented game window " + childId.trim() + " into launcher and mapped");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to embed window via pid", e);
        }
    }

    private static String queryWindowId(String title) {
        try {
            Process p = new ProcessBuilder("xdotool", "search", "--onlyvisible", "--name", title).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String id = br.readLine();
            p.waitFor();
            return id;
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to query window id", e);
            return null;
        }
    }
}

