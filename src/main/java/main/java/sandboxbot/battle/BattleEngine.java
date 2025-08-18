package main.java.sandboxbot.battle;

import java.util.Random;

public class BattleEngine {
    public enum State { IDLE, RUNNING, ENDED }
    public enum Turn { PLAYER, ENEMY }

    private final Random rng = new Random();
    private Combatant player;
    private Combatant enemy;
    private State state = State.IDLE;
    private Turn turn = Turn.PLAYER;
    private String lastLog = "Ready.";

    public void startBattle(Combatant player, Combatant enemy){
        this.player = player;
        this.enemy = enemy;
        this.state = State.RUNNING;
        this.turn = Turn.PLAYER;
        this.lastLog = "A wild " + enemy.name + " appeared!";
    }

    public void reset(){
        state = State.IDLE;
        lastLog = "Ready.";
        player = null;
        enemy = null;
    }

    public State getState(){ return state; }
    public Turn getTurn(){ return turn; }
    public Combatant getPlayer(){ return player; }
    public Combatant getEnemy(){ return enemy; }
    public String getLastLog(){ return lastLog; }

    public void actionAttack(){
        if (state != State.RUNNING) return;
        if (turn != Turn.PLAYER) return;
        int dmg = Math.max(1, player.atk + rng.nextInt(4) - enemy.def/2);
        enemy.setHp(enemy.getHp() - dmg);
        lastLog = player.name + " attacks for " + dmg + " damage!";
        endPlayerTurn();
    }

    public void actionDefend(){
        if (state != State.RUNNING) return;
        if (turn != Turn.PLAYER) return;
        player.def += 2;
        lastLog = player.name + " braces for impact (+DEF)!";
        endPlayerTurn();
        player.def -= 2;
    }

    public void actionPotion(){
        if (state != State.RUNNING) return;
        if (turn != Turn.PLAYER) return;
        if (player.potions <= 0) { lastLog = "No potions left!"; return; }
        player.potions--;
        int heal = Math.min(20, player.maxHp - player.getHp());
        player.setHp(player.getHp() + heal);
        lastLog = player.name + " used a Potion (+" + heal + " HP).";
        endPlayerTurn();
    }

    private void endPlayerTurn(){
        if (enemy.isFainted()) {
            state = State.ENDED;
            lastLog += " " + enemy.name + " fainted!";
            return;
        }
        turn = Turn.ENEMY;
        enemyAct();
    }

    private void enemyAct(){
        if (state != State.RUNNING) return;
        int roll = rng.nextInt(100);
        if (roll < 80) {
            int dmg = Math.max(1, enemy.atk + rng.nextInt(3) - player.def/2);
            player.setHp(player.getHp() - dmg);
            lastLog = enemy.name + " attacks for " + dmg + " damage!";
        } else {
            lastLog = enemy.name + " postures defensively.";
        }
        if (player.isFainted()) {
            state = State.ENDED;
            lastLog += " " + player.name + " fainted!";
        } else {
            turn = Turn.PLAYER;
        }
    }
}
