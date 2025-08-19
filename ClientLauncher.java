import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import plugins.Plugin;

public class ClientLauncher {
    private static final File BASE_DIR = new File(System.getProperty("user.dir"));
    private static final Logger LOGGER = Logger.getLogger(ClientLauncher.class.getName());
    private static final String CLIENT_WINDOW_TITLE = "PokeMMO";
    private static JTextArea logArea;

    static {
        try {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s: %5$s%6$s%n");
            Logger rootLogger = Logger.getLogger("");
            for (Handler h : rootLogger.getHandlers()) {
                h.setFormatter(new SimpleFormatter());
            }
            FileHandler handler = new FileHandler(new File(BASE_DIR, "launcher.log").getAbsolutePath(), true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setLevel(Level.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Plugin> loadPlugins() {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        List<Plugin> plugins = new ArrayList<Plugin>();
        for (Plugin p : loader) {
            plugins.add(p);
        }
        Collections.sort(plugins, new Comparator<Plugin>() {
            @Override
            public int compare(Plugin a, Plugin b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        return plugins;
    }

    private static JPanel buildPluginPanel(List<Plugin> plugins) {
        JPanel pluginPanel = new JPanel();
        pluginPanel.setLayout(new BoxLayout(pluginPanel, BoxLayout.Y_AXIS));
        pluginPanel.setBorder(BorderFactory.createTitledBorder("Plugins"));
        for (Plugin plugin : plugins) {
            final Plugin p = plugin;
            final JCheckBox toggle = new JCheckBox(p.getName());
            toggle.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (toggle.isSelected()) {
                        LOGGER.info("Starting plugin: " + p.getName());
                        p.start();
                    } else {
                        LOGGER.info("Stopping plugin: " + p.getName());
                        p.stop();
                    }
                }
            });
            pluginPanel.add(toggle);
        }
        return pluginPanel;
    }

    private static JFrame buildLauncherFrame() {
        final JFrame frame = new JFrame("PokeMMO Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final List<Plugin> plugins = loadPlugins();
        LOGGER.info("Loaded " + plugins.size() + " plugins");
        final JPanel pluginPanel = buildPluginPanel(plugins);
        final JButton toggleBtn = new JButton("Hide Plugins");
        toggleBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                boolean visible = pluginPanel.isVisible();
                pluginPanel.setVisible(!visible);
                toggleBtn.setText(visible ? "Show Plugins" : "Hide Plugins");
                LOGGER.info("Plugin panel " + (visible ? "hidden" : "shown"));
                frame.revalidate();
                frame.repaint();
            }
        });

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(pluginPanel, BorderLayout.WEST);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(toggleBtn, BorderLayout.WEST);
        bottom.add(logScroll, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Handler uiHandler = new TextAreaHandler(logArea);
        uiHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(uiHandler);

        return frame;
    }

    private static void launchAndEmbed(final JFrame hostFrame) {
        LOGGER.info("Launching PokeMMO client");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb;
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        pb = new ProcessBuilder(new File(BASE_DIR, "PokeMMO.exe").getAbsolutePath());
                    } else {
                        pb = new ProcessBuilder("./PokeMMO.sh");
                    }
                    pb.directory(BASE_DIR);
                    Process proc = pb.start();
                    // Log the process ID explicitly as a string to avoid any type ambiguity.
                    LOGGER.info("Started PokeMMO with pid " + String.valueOf(proc.pid()));

                    final long pid = proc.pid();



                    LOGGER.info("Started PokeMMO with pid " + proc.pid());

                    long pid = proc.pid();
                    LOGGER.info("Started PokeMMO with pid " + pid);


                    // Attempt to embed the client window without blocking its startup.
                    // Running the embedder asynchronously prevents the launcher from
                    // hanging until it is closed before the game becomes visible.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ignored) {
                            }
                            // Embed the client by its process id
                            PidEmbedder.reparent(pid, hostFrame);

                            // Embed the client by its window title rather than PID
                            PidEmbedder.reparent(CLIENT_WINDOW_TITLE, hostFrame);

                          
                            // Embed the client by its window title rather than PID
                            PidEmbedder.reparent(CLIENT_WINDOW_TITLE, hostFrame);
                            PidEmbedder.reparent(CLIENT_WINDOW_TITLE, hostFrame);


                            PidEmbedder.reparent("PokeMMO", hostFrame);

                            PidEmbedder.reparent(pid, hostFrame);

                        }
                    }).start();

                    proc.waitFor();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to launch PokeMMO", e);
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        LOGGER.info("Launcher started");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = buildLauncherFrame();
                launchAndEmbed(frame);
            }
        });
    }

    private static class TextAreaHandler extends Handler {
        private final JTextArea area;

        TextAreaHandler(JTextArea area) {
            this.area = area;
        }

        @Override
        public void publish(final LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    area.append(getFormatter().format(record));
                }
            });
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
