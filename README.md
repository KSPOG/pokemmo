# PokeMMO Launcher

This project provides a Java-based launcher for the PokeMMO client. It embeds the game's window inside a Swing `JFrame` and exposes a simple plugin system.

## Features
- Lists and toggles plugins discovered via Java's `ServiceLoader`.
- Logs to `launcher.log` and a text area in the UI using a concise time/level format.

 - On Linux systems with [xdotool](https://www.semicomplete.com/projects/xdotool/) installed, the launcher attempts to inject the PokeMMO client by process ID and reparent its window into the launcher frame.

- On Linux systems with [xdotool](https://www.semicomplete.com/projects/xdotool/) installed, the launcher attempts to reparent the PokeMMO client window into the launcher frame.


## Requirements
- Java 11 or later (uses `Process.pid()` and `ServiceLoader`).
- PokeMMO client files (`PokeMMO.exe` on Windows or `PokeMMO.sh` on Linux) must reside in the project directory.
- Linux users who want the client embedded must have `xdotool` available in `PATH`.

## Building
Compile the launcher and plugin interface:

```bash
javac ClientLauncher.java PidEmbedder.java plugins/Plugin.java
```

## Running
Start the launcher with:

```bash
java ClientLauncher
```


 The launcher window displays available plugins on the left and logs at the bottom. When the PokeMMO client starts, its window is reparented into the launcher by PID (on supported systems) after a short delay.
The launcher window displays available plugins on the left and logs at the bottom. When the PokeMMO client starts, its window is reparented into the launcher (on supported systems) after a short delay.


## Plugin Development
Plugins implement `plugins.Plugin` and are discovered via the standard Java service provider mechanism. Ensure your plugin jar contains a `META-INF/services/plugins.Plugin` file listing its implementation class.

