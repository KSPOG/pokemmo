package main.java.sandboxbot;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {
    private SaveManager saveManager;
    private SettingsManager settingsManager;
    private ToastManager toastManager;
    private JPanel pluginContainer;

    public ClientFrame() {
        super("SandboxBot Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        saveManager = new SaveManager(this);
        settingsManager = new SettingsManager();
        toastManager = new ToastManager(this);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem quickSaveItem = new JMenuItem("Quick Save");
        quickSaveItem.addActionListener(e -> saveManager.quickSave());
        fileMenu.add(quickSaveItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem toggleAutosaveToast = new JCheckBoxMenuItem("Show Auto-Save Notifications", true);
        toggleAutosaveToast.addActionListener(e ->
                settingsManager.setShowAutosaveToast(((JCheckBoxMenuItem)toggleAutosaveToast).isSelected()));
        settingsMenu.add(toggleAutosaveToast);

        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    public ClientFrame(BotCore bot, World world, int cellSize, int fps) {
        this();
        initPlugins(bot);
        Visualizer viz = new Visualizer(world, cellSize);
        add(viz, BorderLayout.CENTER);
        new Timer(1000 / fps, e -> viz.repaint()).start();
    }

    private void initPlugins(BotCore bot) {
        pluginContainer = new JPanel(new BorderLayout());
        PluginPanel panel = new PluginPanel(bot);
        JScrollPane scroll = new JScrollPane(panel);
        JButton collapse = new JButton("Hide Plugins");
        collapse.addActionListener(e -> {
            boolean visible = scroll.isVisible();
            scroll.setVisible(!visible);
            collapse.setText(visible ? "Show Plugins" : "Hide Plugins");
            revalidate();
        });
        pluginContainer.add(collapse, BorderLayout.NORTH);
        pluginContainer.add(scroll, BorderLayout.CENTER);
        add(pluginContainer, BorderLayout.WEST);
    }

    public ToastManager getToastManager() {
        return toastManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientFrame frame = new ClientFrame();
            frame.setVisible(true);
        });
    }
}
