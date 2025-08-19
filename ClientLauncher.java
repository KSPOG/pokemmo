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
import plugins.Plugin;

public class ClientLauncher {
    private static final File BASE_DIR = new File(System.getProperty("user.dir"));

    private static String[] listPlugins() {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        List<String> names = new ArrayList<>();
        for (Plugin p : loader) {
            names.add(p.getName());
        }
        Collections.sort(names);
        return names.toArray(new String[0]);
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

        final JPanel pluginPanel = buildPluginPanel();
        final JButton toggleBtn = new JButton("Hide Plugins");
        toggleBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                boolean visible = pluginPanel.isVisible();
                pluginPanel.setVisible(!visible);
                toggleBtn.setText(visible ? "Show Plugins" : "Hide Plugins");
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
                    e.printStackTrace();
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
                JFrame frame = buildLauncherFrame();
                launchAndEmbed(frame);
            }
        });
    }
}
