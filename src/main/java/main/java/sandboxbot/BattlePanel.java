package main.java.sandboxbot;

import main.java.sandboxbot.battle.BattleEngine;
import main.java.sandboxbot.battle.Combatant;

import javax.swing.*;
import java.awt.*;

public class BattlePanel extends JPanel implements BattleService.Listener {
    private final JLabel lblPlayer = new JLabel("Player");
    private final JProgressBar hpPlayer = new JProgressBar(0, 100);
    private final JLabel lblEnemy = new JLabel("Enemy");
    private final JProgressBar hpEnemy = new JProgressBar(0, 100);
    private final JTextArea logArea = new JTextArea(6, 22);

    public BattlePanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(28,28,34));

        JPanel top = new JPanel(new GridLayout(2,1,8,8));
        top.setOpaque(false);

        JPanel rowPlayer = new JPanel(new BorderLayout(8,0));
        rowPlayer.setOpaque(false);
        lblPlayer.setForeground(new Color(230,230,245));
        rowPlayer.add(lblPlayer, BorderLayout.WEST);
        styleBar(hpPlayer);
        rowPlayer.add(hpPlayer, BorderLayout.CENTER);

        JPanel rowEnemy = new JPanel(new BorderLayout(8,0));
        rowEnemy.setOpaque(false);
        lblEnemy.setForeground(new Color(230,230,245));
        rowEnemy.add(lblEnemy, BorderLayout.WEST);
        styleBar(hpEnemy);
        rowEnemy.add(hpEnemy, BorderLayout.CENTER);

        top.add(rowPlayer);
        top.add(rowEnemy);

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(24,24,28));
        logArea.setForeground(new Color(220,220,235));
        JScrollPane sp = new JScrollPane(logArea);
        sp.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
    }

    private void styleBar(JProgressBar bar){
        bar.setStringPainted(true);
        bar.setForeground(new Color(90, 210, 140));
        bar.setBackground(new Color(50,54,62));
    }

    public void bind(BattleService service){
        service.addListener(this);
        onUpdate(service.getEngine(), "Ready.");
        onStateChanged(service.getEngine().getState());
    }

    @Override
    public void onUpdate(BattleEngine engine, String log) {
        Combatant p = engine.getPlayer();
        Combatant e = engine.getEnemy();
        if (p != null) {
            lblPlayer.setText(p.name);
            hpPlayer.setValue(p.hpPercent());
            hpPlayer.setString(p.getHp() + "/" + p.maxHp);
        } else {
            hpPlayer.setValue(0); hpPlayer.setString("--");
        }
        if (e != null) {
            lblEnemy.setText(e.name);
            hpEnemy.setValue(e.hpPercent());
            hpEnemy.setString(e.getHp() + "/" + e.maxHp);
        } else {
            hpEnemy.setValue(0); hpEnemy.setString("--");
        }
        logArea.append(log + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    @Override
    public void onStateChanged(BattleEngine.State state) {
        if (state == BattleEngine.State.ENDED) {
            logArea.append("Battle ended.\n");
        }
    }
}
