package main.java.sandboxbot.plugins;

import main.java.sandboxbot.*;

import java.util.List;
import java.util.Random;

public class RandomWalker implements Plugin {
    private final Random rng = new Random();
    private List<World.Pos> path = java.util.List.of();
    private int pathIdx = 0;

    @Override public String name(){ return "RandomWalker"; }

    @Override public void onStart(BotContext ctx) {
        ctx.log.info("Starting RandomWalker");
        ctx.scheduler.every(1500, () -> {
            World.Pos target = new World.Pos(rng.nextInt(ctx.world.width()), rng.nextInt(ctx.world.height()));
            path = ctx.world.path(ctx.world.agent(), target);
            pathIdx = 0;
            ctx.log.info("New target " + target + " pathLen=" + path.size());
        });
    }

    @Override public void onTick(BotContext ctx) {
        if (GameState.isInBattle()) return;
        if (pathIdx < path.size()) {
            ctx.world.moveAgent(path.get(pathIdx++));
        }
    }

    @Override public void onStop(BotContext ctx) { ctx.log.info("Stopping RandomWalker"); }
}
