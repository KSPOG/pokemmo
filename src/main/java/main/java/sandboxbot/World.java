package main.java.sandboxbot;

import java.util.*;
import java.util.function.Consumer;

public class World {
    public enum Tile { EMPTY, WALL, GRASS }

    public static class Pos { 
        public final int x,y; 
        public Pos(int x,int y){this.x=x;this.y=y;}
        @Override public boolean equals(Object o){ if(!(o instanceof Pos))return false; Pos p=(Pos)o; return p.x==x&&p.y==y;}
        @Override public int hashCode(){ return java.util.Objects.hash(x,y);}
        @Override public String toString(){ return "("+x+","+y+")"; }
    }

    private final int w,h;
    private final Tile[][] tiles;
    private Pos agent = new Pos(0,0);
    private final java.util.Random rng = new java.util.Random();

    private int grassEncounterChance = 10; // percent (0-100)
    private Consumer<Pos> encounterListener = null;

    public World(int w,int h){
        this.w=w; this.h=h; 
        this.tiles=new Tile[w][h];
        for(int x=0;x<w;x++) for(int y=0;y<h;y++) tiles[x][y]=Tile.EMPTY;
    }

    public void setEncounterListener(Consumer<Pos> l){ this.encounterListener = l; }
    public void setGrassEncounterChance(int percent){ this.grassEncounterChance = Math.max(0, Math.min(100, percent)); }
    public int getGrassEncounterChance(){ return grassEncounterChance; }

    public int width(){ return w; } 
    public int height(){ return h; }

    public boolean inBounds(Pos p){ return p.x>=0 && p.x<w && p.y>=0 && p.y<h; }
    public Tile getTile(Pos p){ return tiles[p.x][p.y]; }
    public void setTile(int x,int y, Tile t){ tiles[x][y]=t; }
    public boolean isBlocked(Pos p){ return tiles[p.x][p.y] == Tile.WALL; }

    public Pos agent(){ return agent; }

    public void moveAgent(Pos p){ 
        if(!inBounds(p) || isBlocked(p)) return;
        agent = p;
        if (tiles[p.x][p.y] == Tile.GRASS) {
            int roll = rng.nextInt(100);
            if (roll < grassEncounterChance && encounterListener != null) {
                encounterListener.accept(p);
            }
        }
    }

    public java.util.List<Pos> neighbors(Pos p){
        int[][] dirs={{1,0},{-1,0},{0,1},{0,-1}};
        java.util.List<Pos> out=new java.util.ArrayList<>();
        for (int[] d : dirs){
            Pos n = new Pos(p.x+d[0], p.y+d[1]);
            if (inBounds(n) && !isBlocked(n)) out.add(n);
        }
        return out;
    }

    public java.util.List<Pos> path(Pos start, Pos goal){
        java.util.Map<Pos,Pos> prev = new java.util.HashMap<>();
        java.util.ArrayDeque<Pos> q = new java.util.ArrayDeque<>();
        q.add(start); prev.put(start, null);
        while(!q.isEmpty()){
            Pos cur=q.removeFirst();
            if(cur.equals(goal)) break;
            for(Pos n: neighbors(cur)){
                if(!prev.containsKey(n)){ prev.put(n, cur); q.add(n); }
            }
        }
        if(!prev.containsKey(goal)) return java.util.Collections.emptyList();
        java.util.List<Pos> path=new java.util.ArrayList<>();
        for(Pos at=goal; at!=null; at=prev.get(at)) path.add(at);
        java.util.Collections.reverse(path);
        return path;
    }
}
