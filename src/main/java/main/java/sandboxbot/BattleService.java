package main.java.sandboxbot;

import main.java.sandboxbot.battle.BattleEngine;
import main.java.sandboxbot.battle.Combatant;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleService {
    public interface Listener {
        void onUpdate(BattleEngine engine, String log);
        void onStateChanged(BattleEngine.State state);
    }

    private final List<Listener> listeners = new ArrayList<>();
    private final Random rng = new Random();
    private final BattleEngine engine = new BattleEngine();
    private volatile boolean autoThreadRunning = false;

    public void addListener(Listener l){ listeners.add(l); }
    public void removeListener(Listener l){ listeners.remove(l); }

    public boolean isInBattle(){
        return engine.getState() == BattleEngine.State.RUNNING;
    }

    public void startRandomBattle(){
        if (isInBattle()) return;
        Combatant player = new Combatant("TrainerMon", 100, 16, 6);
        Combatant enemy  = new Combatant(randomWild(), 70 + rng.nextInt(40), 12 + rng.nextInt(6), 4 + rng.nextInt(4));
        engine.startBattle(player, enemy);
        GameState.setInBattle(true);
        notifyUpdate(engine.getLastLog());
        notifyState();

        startAutoThread();
    }

    private String randomWild(){
        String[] wilds = {"Slime", "Bat", "Boar", "Mantis", "Gecko"};
        return wilds[rng.nextInt(wilds.length)];
    }

    private void startAutoThread(){
        if (autoThreadRunning) return;
        autoThreadRunning = true;
        new Thread(() -> {
            try {
                while (engine.getState() == BattleEngine.State.RUNNING) {
                    Thread.sleep(600);
                    if (engine.getPlayer().hpPercent() <= 35 && engine.getPlayer().potions > 0) {
                        engine.actionPotion();
                    } else {
                        if (rng.nextInt(100) < 15) engine.actionDefend();
                        else engine.actionAttack();
                    }
                    String log = engine.getLastLog();
                    notifyUpdate(log);
                    Thread.sleep(600);
                }
            } catch (InterruptedException ignored) {
            } finally {
                autoThreadRunning = false;
                GameState.setInBattle(false);
                notifyState();
            }
        }, "AutoBattle").start();
    }

    private void notifyUpdate(String log){
        for (Listener l : new ArrayList<>(listeners)) {
            SwingUtilities.invokeLater(() -> l.onUpdate(engine, log));
        }
    }
    private void notifyState(){
        for (Listener l : new ArrayList<>(listeners)) {
            SwingUtilities.invokeLater(() -> l.onStateChanged(engine.getState()));
        }
    }

    public BattleEngine getEngine(){ return engine; }
}
