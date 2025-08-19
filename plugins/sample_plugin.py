class Plugin:
    """Simple example plugin."""

    def run(self, client):
        client.logger.info("Sample plugin ran")
