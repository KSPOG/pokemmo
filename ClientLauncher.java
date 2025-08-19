import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Comparator;
import java.util.logging.*;
import plugins.Plugin;

public class ClientLauncher {
    private static final File BASE_DIR = new File(System.getProperty("user.dir"));
    private static final Logger LOGGER = Logger.getLogger(ClientLauncher.class.getName());

    static {
        try {
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

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(pluginPanel, BorderLayout.WEST);
        content.add(toggleBtn, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    private static void launchAndEmbed(final JFrame hostFrame) {
        LOGGER.info("Launching PokeMMO client");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (URLClassLoader cl = new URLClassLoader(new URL[]{
                        new File(BASE_DIR, "PokeMMO.exe").toURI().toURL()
                })) {
                    Thread current = Thread.currentThread();
                    current.setContextClassLoader(cl);
                    Class<?> clientCls = Class.forName("com.pokeemu.client.Client", true, cl);
                    Method main = clientCls.getMethod("main", String[].class);
                    main.invoke(null, (Object) new String[0]);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to launch PokeMMO", e);
                }
            }
        }).start();

        final Timer timer = new Timer(500, null);
        timer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                for (Frame f : Frame.getFrames()) {
                    if (f.isVisible() && f != hostFrame) {
                        timer.stop();
                        Container gameContent = (f instanceof JFrame)
                                ? ((JFrame) f).getContentPane()
                                : f;
                        f.setVisible(false);
                        if (f instanceof JFrame) {
                            ((JFrame) f).setContentPane(new JPanel());
                        } else {
                            f.removeAll();
                        }
                        hostFrame.getContentPane().add(gameContent, BorderLayout.CENTER);
                        hostFrame.revalidate();
                        hostFrame.repaint();
                        f.dispose();
                        LOGGER.info("Embedded game window into launcher");
                        break;
                    }
                }
            }
        });
        timer.start();
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
}
