package main.java.sandboxbot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotCore {
    public static class PluginHandle {
        public final Plugin plugin;
        private volatile boolean enabled;
        private volatile boolean started;

        PluginHandle(Plugin plugin, boolean enabled) {
            this.plugin = plugin;
            this.enabled = enabled;
            this.started = false;
        }
        public String name() { return plugin.name(); }
        public boolean isEnabled() { return enabled; }
        public boolean isStarted() { return started; }
    }

    private final List<PluginHandle> handles = new CopyOnWriteArrayList<>();
    private final Scheduler scheduler = new Scheduler();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final int ticksPerSecond;
    private final BotContext ctx;

    public BotCore(int tps, World world) {
        this.ticksPerSecond = Math.max(1, tps);
        this.ctx = new BotContext(world, scheduler, new Logger());
    }

    public BotContext context(){ return ctx; }
    public List<PluginHandle> getHandles() { return handles; }

    public PluginHandle register(Plugin plugin) { return register(plugin, true); }
    public PluginHandle register(Plugin plugin, boolean enabled) {
        PluginHandle h = new PluginHandle(plugin, enabled);
        handles.add(h);
        return h;
    }

    public void setEnabled(PluginHandle handle, boolean enabled) { handle.enabled = enabled; }

    public void start() {
        if (!running.compareAndSet(false, true)) return;
        final long tickMillis = 1000L / ticksPerSecond;

        while (running.get()) {
            long t0 = System.currentTimeMillis();

            for (PluginHandle h : handles) {
                if (h.isEnabled() && !h.started) {
                    safeStart(h);
                } else if (!h.isEnabled() && h.started) {
                    safeStop(h);
                }
            }

            scheduler.tick(ctx);
            for (PluginHandle h : handles) {
                if (h.isEnabled() && h.started) safeTick(h);
            }

            long dt = System.currentTimeMillis() - t0;
            long sleep = Math.max(0, tickMillis - dt);
            try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
        }

        for (PluginHandle h : handles) if (h.started) safeStop(h);
    }

    public void stop() { running.set(false); }

    private void safeStart(PluginHandle h) { try { h.plugin.onStart(ctx); } catch (Throwable t) { log().warn("onStart " + h.name() + ": " + t); } h.started = true; }
    private void safeTick (PluginHandle h) { 
        try { 
            if (!GameState.isInBattle()) {
                h.plugin.onTick (ctx); 
            }
        } catch (Throwable t) { log().warn("onTick  " + h.name() + ": " + t); } 
    }
    private void safeStop (PluginHandle h) { try { h.plugin.onStop (ctx); } catch (Throwable t) { log().warn("onStop  " + h.name() + ": " + t); } h.started = false; }

    public Logger log(){ return ctx.log; }

    public static class Logger {
        public void info(String s){ System.out.println("[INFO] " + s); }
        public void warn(String s){ System.out.println("[WARN] " + s); }
    }
}
