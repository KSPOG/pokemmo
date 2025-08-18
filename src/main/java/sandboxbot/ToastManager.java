package sandboxbot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToastManager extends JPanel {
    private final ClientFrame client;
    private final List<Toast> toasts = new ArrayList<>();
    private String statusIcon = null;
    private long statusIconStartTime = 0;

    public ToastManager(ClientFrame client) {
        this.client = client;
        Timer repaintTimer = new Timer(100, e -> repaint());
        repaintTimer.start();
        client.add(this, BorderLayout.CENTER);
    }

    public void showToast(String message) {
        toasts.add(new Toast(message, System.currentTimeMillis()));
    }

    public void showStatusIcon(String icon) {
        this.statusIcon = icon;
        this.statusIconStartTime = System.currentTimeMillis();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw toasts
        int y = getHeight() - 40;
        Iterator<Toast> it = toasts.iterator();
        while (it.hasNext()) {
            Toast toast = it.next();
            long elapsed = System.currentTimeMillis() - toast.startTime;
            if (elapsed > 2500) {
                it.remove();
                continue;
            }
            g2d.setColor(new Color(0, 0, 0, 170));
            g2d.fillRoundRect(getWidth() - 260, y - 20, 250, 30, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString(toast.message, getWidth() - 250, y);
            y -= 40;
        }

        // Draw status icon
        if (statusIcon != null) {
            long elapsed = System.currentTimeMillis() - statusIconStartTime;
            if (elapsed > 1500) {
                statusIcon = null;
            } else {
                g2d.setColor(Color.WHITE);
                g2d.drawString(statusIcon, getWidth() - 30, getHeight() - 60);
            }
        }
    }

    private static class Toast {
        String message;
        long startTime;
        Toast(String message, long startTime) {
            this.message = message;
            this.startTime = startTime;
        }
    }
}
