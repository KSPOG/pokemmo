package main.java.sandboxbot;

public class BotContext {
    public final World world;
    public final Scheduler scheduler;
    public final BotCore.Logger log;

    public BotContext(World world, Scheduler scheduler, BotCore.Logger log) {
        this.world = world; this.scheduler = scheduler; this.log = log;
    }
}
