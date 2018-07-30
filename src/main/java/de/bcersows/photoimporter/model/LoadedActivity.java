package de.bcersows.photoimporter.model;

import de.bcersows.photoimporter.ui.Activity;
import de.bcersows.photoimporter.ui.Activity.ActivityKey;
import javafx.scene.Parent;

/**
 * Contains a loaded activity.
 * 
 * @author BCE
 */
public class LoadedActivity {
    private final ActivityKey key;
    private final String fxmlPath;
    private final Activity activity;
    private final Parent root;

    public LoadedActivity(final ActivityKey key, final String fxmlPath, final Activity activity, final Parent root) {
        super();
        this.key = key;
        this.fxmlPath = fxmlPath;
        this.activity = activity;
        this.root = root;
    }

    public ActivityKey getKey() {
        return key;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public Activity getActivity() {
        return activity;
    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "LoadedActivity [key=" + key + ", fxmlPath=" + fxmlPath + ", activity=" + activity + "]";
    }

}
