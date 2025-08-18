# SandboxBot — Auto Battle + Encounters + Visualizer + Sidebar
Packages:
- `main.java.sandboxbot` (core + UI)
- `main.java.sandboxbot.plugins` (plugins)
- `main.java.sandboxbot.battle` (battle engine)

## Run (CLI)
```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out main.java.sandboxbot.MainSwing
```

## Notes
- Left sidebar: Map Tools (Wall/Grass/Erase), encounter chance, plugin toggles, Start/Stop.
- Center: Visualizer — click and drag to paint tiles.
- Right: BattlePanel — auto-battle plays out when the agent steps on grass.
- Plugins pause while in battle.
