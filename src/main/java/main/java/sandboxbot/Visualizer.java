package main.java.sandboxbot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Visualizer extends JPanel {
    public enum Tool { WALL, GRASS, ERASE }

    private final World world;
    private final int cellSize;
    private final int margin = 16;
    private Tool tool = Tool.WALL;

    public Visualizer(World world, int cellSize){
        this.world = world;
        this.cellSize = cellSize;
        setPreferredSize(new Dimension(margin*2 + world.width()*cellSize, margin*2 + world.height()*cellSize));
        setBackground(new Color(36,36,42));

        MouseAdapter ma = new MouseAdapter() {
            private boolean painting = false;
            @Override public void mousePressed(MouseEvent e){
                painting = true;
                paintAt(e);
            }
            @Override public void mouseReleased(MouseEvent e){ painting = false; }
            @Override public void mouseDragged(MouseEvent e){ if (painting) paintAt(e); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void setTool(Tool t){ this.tool = t; }

    private void paintAt(MouseEvent e){
        int x = (e.getX() - margin) / cellSize;
        int y = (e.getY() - margin) / cellSize;
        if (x < 0 || y < 0 || x >= world.width() || y >= world.height()) return;
        switch (tool){
            case WALL -> world.setTile(x,y, World.Tile.WALL);
            case GRASS -> world.setTile(x,y, World.Tile.GRASS);
            case ERASE -> world.setTile(x,y, World.Tile.EMPTY);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int y=0; y<world.height(); y++) {
            for (int x=0; x<world.width(); x++) {
                int px = margin + x*cellSize;
                int py = margin + y*cellSize;
                World.Tile t = world.getTile(new World.Pos(x,y));
                Color base = switch (t){
                    case EMPTY -> new Color(50,54,62);
                    case WALL  -> new Color(140, 60, 60);
                    case GRASS -> new Color(60, 140, 80);
                };
                g2.setColor(base);
                g2.fillRect(px, py, cellSize-1, cellSize-1);
            }
        }
        World.Pos a = world.agent();
        int ax = margin + a.x*cellSize;
        int ay = margin + a.y*cellSize;
        g2.setColor(new Color(90, 210, 140));
        g2.fillOval(ax+4, ay+4, cellSize-8, cellSize-8);

        g2.setColor(new Color(210,210,210));
        g2.drawRect(margin-1, margin-1, world.width()*cellSize+1, world.height()*cellSize+1);
    }
}
