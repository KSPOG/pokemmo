package main.java.sandboxbot;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class SaveManager {
    private final ClientFrame client;

    public SaveManager(ClientFrame client) {
        this.client = client;
    }

    public void quickSave() {
        try (FileWriter fw = new FileWriter("quicksave.json")) {
            fw.write("{\"timestamp\": \"" + LocalDateTime.now() + "\"}");
            client.getToastManager().showToast("✅ Game saved (Quick Save)");
            client.getToastManager().showStatusIcon("💾");
        } catch (IOException e) {
            client.getToastManager().showToast("❌ Failed to save game");
        }
    }

    public void autoSave() {
        try (FileWriter fw = new FileWriter("autosave.json")) {
            fw.write("{\"timestamp\": \"" + LocalDateTime.now() + "\"}");
            if (client.getSettingsManager().isShowAutosaveToast()) {
                client.getToastManager().showToast("💾 Auto-saved");
            }
            client.getToastManager().showStatusIcon("💾");
        } catch (IOException e) {
            client.getToastManager().showToast("❌ Failed to auto-save");
        }
    }
}
