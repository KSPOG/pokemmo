import logging
import importlib
import subprocess
from pathlib import Path
from typing import List


class Client:
    """Simple launcher for PokeMMO with plugin support."""

    def __init__(self, game_path: Path | None = None) -> None:
        self.game_path = game_path or self._default_game_path()
        self.logger = self._create_logger()
        self.plugins: List[object] = []

    def _create_logger(self) -> logging.Logger:
        logger = logging.getLogger("pokemmo.client")
        if not logger.handlers:
            logger.setLevel(logging.INFO)
            handler = logging.StreamHandler()
            fmt = "[%(levelname)s] %(name)s: %(message)s"
            handler.setFormatter(logging.Formatter(fmt))
            logger.addHandler(handler)
        return logger

    def _default_game_path(self) -> Path:
        root = Path(__file__).resolve().parent
        if (root / "PokeMMO.exe").exists():
            return root / "PokeMMO.exe"
        return root / "PokeMMO.sh"

    # Plugins -----------------------------------------------------------------
    def load_plugins(self, directory: Path | None = None) -> None:
        """Load all plugins from the provided directory."""
        directory = directory or Path(__file__).resolve().parent / "plugins"
        if not directory.exists():
            self.logger.debug("Plugin directory %s does not exist", directory)
            return
        for file in directory.glob("*.py"):
            if file.name == "__init__.py":
                continue
            module_name = f"plugins.{file.stem}"
            try:
                module = importlib.import_module(module_name)
            except Exception as exc:  # pragma: no cover - best effort logging
                self.logger.error("Failed to import %s: %s", module_name, exc)
                continue
            plugin_cls = getattr(module, "Plugin", None)
            if plugin_cls is None:
                self.logger.warning("No Plugin class in %s", module_name)
                continue
            plugin = plugin_cls()
            self.plugins.append(plugin)
            self.logger.info("Loaded plugin %s", module_name)

    # -------------------------------------------------------------------------
    def run(self) -> None:
        """Start the PokeMMO game."""
        self.logger.info("Launching PokeMMO from %s", self.game_path)
        if self.game_path.suffix == ".sh":
            subprocess.Popen(["bash", str(self.game_path)], cwd=self.game_path.parent)
        else:
            subprocess.Popen([str(self.game_path)], cwd=self.game_path.parent)


if __name__ == "__main__":
    client = Client()
    client.load_plugins()
    for plugin in client.plugins:
        run = getattr(plugin, "run", None)
        if callable(run):
            run(client)
    client.run()
