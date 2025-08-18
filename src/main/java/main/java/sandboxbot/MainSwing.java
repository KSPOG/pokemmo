package main.java.sandboxbot;

import main.java.sandboxbot.plugins.RandomWalker;
import javax.swing.*;

import main.java.sandboxbot.ClientFrame;

public class MainSwing {
    public static void main(String[] args) {
        World world = new World(18, 12);

        BotCore bot = new BotCore(15, world);
        bot.register(new RandomWalker(), true);
        new Thread(bot::start).start();

        SwingUtilities.invokeLater(() -> {
            ClientFrame frame = new ClientFrame(bot, world, 40, 30);
            frame.setVisible(true);
        });
    }
}
