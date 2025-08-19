import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class ClientLauncher {
    private static final File BASE_DIR = new File(System.getProperty("user.dir"));

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

    private static void attachPlugins(JFrame gameFrame) {
        JPanel pluginPanel = buildPluginPanel();
        JButton toggleBtn = new JButton("Hide Plugins");
        toggleBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                boolean visible = pluginPanel.isVisible();
                pluginPanel.setVisible(!visible);
                toggleBtn.setText(visible ? "Show Plugins" : "Hide Plugins");
                gameFrame.pack();
            }
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
                    e.printStackTrace();
                }
            }
        }).start();

        final Timer timer = new Timer(500, null);
        timer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                for (Frame f : Frame.getFrames()) {
                    if (f instanceof JFrame && f.isVisible() && !"PokeMMO Launcher".equals(f.getTitle())) {
                        timer.stop();
                        attachPlugins((JFrame) f);
                        break;
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
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
        });
    }
}
