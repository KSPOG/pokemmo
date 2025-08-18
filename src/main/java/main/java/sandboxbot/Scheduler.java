package main.java.sandboxbot;

import java.util.*;

public class Scheduler {
    private static class Job {
        long nextAtMs;
        final long everyMs;
        final Runnable task;
        Job(long nextAtMs, long everyMs, Runnable task){
            this.nextAtMs=nextAtMs; this.everyMs=everyMs; this.task=task;
        }
    }
    private final List<Job> jobs = new ArrayList<>();
    public void every(long periodMs, Runnable task){
        jobs.add(new Job(System.currentTimeMillis()+periodMs, periodMs, task));
    }
    void tick(BotContext ctx){
        long now = System.currentTimeMillis();
        for (Job j : jobs) {
            if (now >= j.nextAtMs) {
                try { j.task.run(); } catch (Throwable t) { ctx.log.warn("Task error: "+t); }
                j.nextAtMs = now + j.everyMs;
            }
        }
    }
}
