#!/usr/bin/env python3
import os
import subprocess
import sys
import tkinter as tk
from tkinter import ttk

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
JAVA_CMD = os.path.join(BASE_DIR, 'jre', 'bin', 'java')
if not os.path.exists(JAVA_CMD):
    JAVA_CMD = 'java'
CLASSPATH = os.path.join(BASE_DIR, 'PokeMMO.exe')
JVM_OPTS = ['-Xmx384M', '-Dfile.encoding=UTF-8']
CLIENT_CLASS = 'com.pokeemu.client.Client'


def list_plugins():
    """Return a sorted list of plugin names in the plugins directory."""
    plugins_dir = os.path.join(BASE_DIR, 'plugins')
    if not os.path.isdir(plugins_dir):
        return []
    return sorted(os.listdir(plugins_dir))


def launch_pokemmo():
    """Launch the PokeMMO Java client."""
    cmd = [JAVA_CMD, *JVM_OPTS, '-cp', CLASSPATH, CLIENT_CLASS]
    try:
        subprocess.Popen(cmd, cwd=BASE_DIR)
    except OSError as exc:
        print(f"Failed to launch PokeMMO: {exc}", file=sys.stderr)


def main():
    """Start the graphical launcher with a hideable plugin list."""
    root = tk.Tk()
    root.title('PokeMMO Launcher')

    container = ttk.Frame(root)
    container.pack(fill='both', expand=True)

    plugin_frame = ttk.Frame(container, borderwidth=1, relief='sunken')
    plugin_frame.pack(side='left', fill='y')

    ttk.Label(plugin_frame, text='Plugins').pack(anchor='nw')
    plugin_list = tk.Listbox(plugin_frame, height=10)
    plugin_list.pack(fill='both', expand=True)
    for name in list_plugins():
        plugin_list.insert('end', name)

    main_frame = ttk.Frame(container)
    main_frame.pack(side='left', fill='both', expand=True)

    ttk.Button(main_frame, text='Launch PokeMMO', command=launch_pokemmo).pack(pady=10)

    def toggle_plugins():
        if plugin_frame.winfo_manager():
            plugin_frame.pack_forget()
            toggle_btn.config(text='Show Plugins')
        else:
            plugin_frame.pack(side='left', fill='y')
            toggle_btn.config(text='Hide Plugins')

    toggle_btn = ttk.Button(main_frame, text='Hide Plugins', command=toggle_plugins)
    toggle_btn.pack(pady=5)

    root.mainloop()


if __name__ == '__main__':
    main()
