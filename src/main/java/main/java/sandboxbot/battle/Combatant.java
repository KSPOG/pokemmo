package main.java.sandboxbot.battle;

public class Combatant {
    public final String name;
    public final int maxHp;
    private int hp;
    public int atk;
    public int def;
    public int potions = 2;

    public Combatant(String name, int maxHp, int atk, int def) {
        this.name = name;
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
        this.hp = maxHp;
    }
    public int getHp(){ return hp; }
    public void setHp(int v){ hp = Math.max(0, Math.min(maxHp, v)); }
    public boolean isFainted(){ return hp <= 0; }
    public int hpPercent(){ return Math.round(100f * hp / Math.max(1, maxHp)); }
}
