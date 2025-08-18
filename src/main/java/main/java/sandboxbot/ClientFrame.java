package main.java.sandboxbot;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {
    private SaveManager saveManager;
    private SettingsManager settingsManager;
    private ToastManager toastManager;

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
