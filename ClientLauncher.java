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

                        p.start();
                    } else {

                        p.stop();
                    }
                }
            });
            pluginPanel.add(toggle);
        }
        return pluginPanel;
    }


import plugins.Plugin;


import java.io.IOException;
import java.util.Arrays;

public class ClientLauncher {
    private static final File BASE_DIR = new File(System.getProperty("user.dir"));


    private static String[] listPlugins() {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        List<String> names = new ArrayList<>();
        for (Plugin p : loader) {
            names.add(p.getName());
        }
        Collections.sort(names);


    private static String resolveJavaCmd() {
        File bundled = new File(BASE_DIR, "jre/bin/java");
        return bundled.exists() ? bundled.getPath() : "java";
    }

    private static String[] listPlugins() {
        File dir = new File(BASE_DIR, "plugins");
        if (dir.isDirectory()) {
            String[] names = dir.list();
            if (names != null) {
                Arrays.sort(names);
                return names;
            }
        }
        return new String[0];

    }

    private static JPanel buildPluginPanel() {
        JPanel pluginPanel = new JPanel(new BorderLayout());
        pluginPanel.setBorder(BorderFactory.createTitledBorder("Plugins"));
        JList<String> pluginList = new JList<>(listPlugins());
        pluginPanel.add(new JScrollPane(pluginList), BorderLayout.CENTER);
        return pluginPanel;
    }



    private static JFrame buildLauncherFrame() {
        final JFrame frame = new JFrame("PokeMMO Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final List<Plugin> plugins = loadPlugins();

        LOGGER.info("Loaded " + plugins.size() + " plugins");
        final JPanel pluginPanel = buildPluginPanel(plugins);
        final JButton toggleBtn = new JButton("Hide Plugins");

        final JPanel pluginPanel = buildPluginPanel(plugins);
        final JButton toggleBtn = new JButton("Hide Plugins");
        final JPanel pluginPanel = buildPluginPanel();
        final JButton toggleBtn = new JButton("Hide Plugins");

    private static void attachPlugins(JFrame gameFrame) {
        final JPanel pluginPanel = buildPluginPanel();
        final JButton toggleBtn = new JButton("Hide Plugins");
        JPanel pluginPanel = buildPluginPanel();
        JButton toggleBtn = new JButton("Hide Plugins");


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


                gameFrame.pack();
            }

    private static void launchPokemmo() {
        String java = resolveJavaCmd();
        File classPath = new File(BASE_DIR, "PokeMMO.exe");
        ProcessBuilder pb = new ProcessBuilder(
                java,
                "-Xmx384M",
                "-Dfile.encoding=UTF-8",
                "-cp",
                classPath.getPath(),
                "com.pokeemu.client.Client"
        );
        pb.directory(BASE_DIR);
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openGameWindow() {
        JFrame gameFrame = new JFrame("PokeMMO");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());

        JPanel pluginPanel = buildPluginPanel();
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBorder(BorderFactory.createTitledBorder("Game"));
        gamePanel.add(new JLabel("PokeMMO running...", SwingConstants.CENTER), BorderLayout.CENTER);

        JButton toggleBtn = new JButton("Hide Plugins");
        toggleBtn.addActionListener(e -> {
            boolean visible = pluginPanel.isVisible();
            pluginPanel.setVisible(!visible);
            toggleBtn.setText(visible ? "Show Plugins" : "Hide Plugins");
            gameFrame.pack();
        });

        Container content = gameFrame.getContentPane();
        content.add(pluginPanel, BorderLayout.WEST);
        content.add(toggleBtn, BorderLayout.SOUTH);
        gameFrame.pack();
    }

    private static void launchAndEmbed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL jarUrl = new File(BASE_DIR, "PokeMMO.exe").toURI().toURL();
                    URLClassLoader cl = new URLClassLoader(new URL[]{jarUrl});


                    Class<?> clientCls = Class.forName("com.pokeemu.client.Client", true, cl);
                    Method main = clientCls.getMethod("main", String[].class);
                    main.invoke(null, (Object) new String[0]);
                } catch (Exception e) {

                    LOGGER.log(Level.SEVERE, "Failed to launch PokeMMO", e);

                    e.printStackTrace();

                }
            }
        }).start();

        final Timer timer = new Timer(500, null);


        new Thread(() -> {
            try {
                URL jarUrl = new File(BASE_DIR, "PokeMMO.exe").toURI().toURL();
                URLClassLoader cl = new URLClassLoader(new URL[]{jarUrl});
                Class<?> clientCls = Class.forName("com.pokeemu.client.Client", true, cl);
                Method main = clientCls.getMethod("main", String[].class);
                main.invoke(null, (Object) new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Timer timer = new Timer(500, null);


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

                        break;
                    }
                    if (f instanceof JFrame && f.isVisible() && !"PokeMMO Launcher".equals(f.getTitle())) {
                        timer.stop();
                        attachPlugins((JFrame) f);
                        break;
                    }
        timer.addActionListener(e -> {
            for (Frame f : Frame.getFrames()) {
                if (f instanceof JFrame && f.isVisible() && !"PokeMMO Launcher".equals(f.getTitle())) {
                    timer.stop();
                    attachPlugins((JFrame) f);
                    break;

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

                final JFrame frame = new JFrame("PokeMMO Launcher");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JButton launchButton = new JButton("Launch PokeMMO");
                launchButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        frame.dispose();
                        launchAndEmbed();
                    }
                });
                frame.add(launchButton);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            gameFrame.revalidate();
        });

        gameFrame.add(pluginPanel, BorderLayout.WEST);
        gameFrame.add(gamePanel, BorderLayout.CENTER);
        gameFrame.add(toggleBtn, BorderLayout.SOUTH);

        gameFrame.setSize(800, 600);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        launchPokemmo();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PokeMMO Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JButton launchButton = new JButton("Launch PokeMMO");
            launchButton.addActionListener(e -> {
                frame.dispose();
                launchAndEmbed();
            });
            frame.add(launchButton);

                openGameWindow();
            });
            frame.add(launchButton);

            JButton launchBtn = new JButton("Launch PokeMMO");
            launchBtn.addActionListener(e -> {
                frame.dispose();
                openGameWindow();
            });
            frame.add(launchBtn);

            frame.setLayout(new BorderLayout());

            JPanel pluginPanel = new JPanel(new BorderLayout());
            pluginPanel.setBorder(BorderFactory.createTitledBorder("Plugins"));
            JList<String> pluginList = new JList<>(listPlugins());
            pluginPanel.add(new JScrollPane(pluginList), BorderLayout.CENTER);

            JPanel mainPanel = new JPanel();
            JButton launchBtn = new JButton("Launch PokeMMO");
            launchBtn.addActionListener(e -> launchPokemmo());
            mainPanel.add(launchBtn);

            JButton toggleBtn = new JButton("Hide Plugins");
            toggleBtn.addActionListener(e -> {
                boolean visible = pluginPanel.isVisible();
                pluginPanel.setVisible(!visible);
                toggleBtn.setText(visible ? "Show Plugins" : "Hide Plugins");
                frame.revalidate();
            });
            mainPanel.add(toggleBtn);

            frame.add(pluginPanel, BorderLayout.WEST);
            frame.add(mainPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });
    }
}
