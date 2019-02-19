package de.bcersows.photoimporter.ui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bcersows.photoimporter.model.ToolSettings;
import de.bcersows.photoimporter.texts.TextDefinition;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;

/**
 * Activity base class.
 * 
 * @author BCE
 */
public abstract class Activity {
    /** UI controller for the scene. **/
    protected final UiController uiController;

    /**
     * Create base class.
     */
    public Activity(@Nonnull final UiController uiController) {
        this.uiController = uiController;
    }

    /** Initialize this activity. **/
    @FXML
    public abstract void initialize();

    /** Terminate this activity. **/
    public abstract void terminate();

    /** Run after activity was shown, e.g. to collect data. Activity override. **/
    protected abstract void postShowActivity();

    /** Get the key of this activity. **/
    public abstract ActivityKey getActivityKey();

    /** Return the action to set to apply button. **/
    @Nullable
    protected abstract Runnable getButtonActionApply();

    /** Return the action to set to retry button. **/
    @Nullable
    protected abstract Runnable getButtonActionRetry();

    /** Run after activity was shown, e.g. to collect data. **/
    public final void postShow() {
        this.uiController.setButtonActions(getButtonActionApply(), getButtonActionRetry());
        this.postShowActivity();
    }

    /** Get the application settings. **/
    protected final ToolSettings getApplicationSettings() {
        return this.uiController.getApplicationSettings();
    }

    /**
     * Return the size as String.
     */
    protected final String getSize(final Region region) {
        return getSize(region.getWidth(), region.getHeight());
    }

    /**
     * Return the size as String.
     */
    protected final String getSize(final double width, final double height) {
        return TextDefinition.BRACKET_OPEN + width + TextDefinition.SLASH + height + TextDefinition.BRACKET_CLOSE;
    }

    /** The possible activities. **/
    public enum ActivityKey {
        /** Import photos. **/
        IMPORT(PhotoImportController.class, "/fxml/PhotoImport.fxml"),
        /** Show list of events. **/
        EVENTS(EventsController.class, "/fxml/Events.fxml");

        private final Class<? extends Activity> activityClass;
        private final String fxmlPath;

        private ActivityKey(final Class<? extends Activity> activityClass, final String fxmlPath) {
            this.activityClass = activityClass;
            this.fxmlPath = fxmlPath;
        }

        public Class<? extends Activity> getActivityClass() {
            return this.activityClass;
        }

        public String getFxmlPath() {
            return this.fxmlPath;
        }
    }

    @Override
    public String toString() {
        return "Activity [key=" + getActivityKey() + "]";
    }

}
