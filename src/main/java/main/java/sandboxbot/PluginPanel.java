package main.java.sandboxbot;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that lists all plugins and allows toggling them on/off.
 */
public class PluginPanel extends JPanel {
    public PluginPanel(BotCore bot) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (BotCore.PluginHandle h : bot.getHandles()) {
            JCheckBox box = new JCheckBox(h.name(), h.isEnabled());
            box.addActionListener(e -> bot.setEnabled(h, box.isSelected()));
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(box);
        }
    }
}
