package main.java.sandboxbot;

public class GameState {
    private static volatile boolean inBattle = false;
    public static boolean isInBattle(){ return inBattle; }
    public static void setInBattle(boolean v){ inBattle = v; }
}
