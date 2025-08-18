package main.java.sandboxbot;

import main.java.sandboxbot.plugins.RandomWalker;
import sandboxbot.ClientFrame;
import javax.swing.*;

public class MainSwing {
    public static void main(String[] args) {
        World world = new World(18, 12);

        BotCore bot = new BotCore(15, world);
        bot.register(new RandomWalker(), true);
        new Thread(bot::start).start();

        SwingUtilities.invokeLater(() -> {
            ClientFrame frame = new ClientFrame();
            frame.setVisible(true);
        });
    }
}
