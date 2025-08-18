package main.java.main.java.sandboxbot;

import main.java.sandboxbot.plugins.RandomWalker;
import javax.swing.*;

public class MainSwing {
    public static void main(String[] args) {
        main.java.sandboxbot.World world = new main.java.sandboxbot.World(18, 12);

        main.java.sandboxbot.BotCore bot = new main.java.sandboxbot.BotCore(15, world);
        bot.register(new RandomWalker(), true);

        SwingUtilities.invokeLater(() -> new ClientFrame(bot, world, 40, 30));
    }
}
