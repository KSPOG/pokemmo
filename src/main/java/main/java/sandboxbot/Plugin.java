package main.java.sandboxbot;

public interface Plugin {
    String name();
    void onStart(BotContext ctx);
    void onTick(BotContext ctx);
    void onStop(BotContext ctx);
}
