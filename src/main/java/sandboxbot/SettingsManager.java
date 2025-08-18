package sandboxbot;

public class SettingsManager {
    private boolean showAutosaveToast = true;

    public boolean isShowAutosaveToast() {
        return showAutosaveToast;
    }

    public void setShowAutosaveToast(boolean show) {
        this.showAutosaveToast = show;
    }
}
