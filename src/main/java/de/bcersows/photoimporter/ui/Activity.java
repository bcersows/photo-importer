package de.bcersows.photoimporter.ui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bcersows.photoimporter.ToolConstants;
import de.bcersows.photoimporter.model.ToolSettings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Activity base class.
 * 
 * @author BCE
 */
public abstract class Activity {
    /** A log message for when an UI event was caused. **/
    public static final String LOG_MESSAGE_EVENT = "Caused event: {}.";
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

    /** Run after activity was shown, e.g. to collect data. **/
    public final void postShow() {
        this.uiController.setButtonActions(getButtonApplyAction(), getButtonReloadAction());
        this.uiController.setButtonDisableProperties(getButtonApplyDisableProperty(), getButtonReloadDisableProperty());
        this.postShowActivity();
    }

    /** Get the application settings. **/
    protected final ToolSettings getApplicationSettings() {
        return this.uiController.getApplicationSettings();
    }

    /** Return the action to set to apply button. **/
    @Nullable
    protected Runnable getButtonApplyAction() {
        return null;
    }

    /** Return the action to set to reload button. **/
    @Nullable
    protected Runnable getButtonReloadAction() {
        return null;
    }

    /** Return the property to disable the apply button. **/
    @Nullable
    protected ObservableBooleanValue getButtonApplyDisableProperty() {
        return null;
    }

    /** Return the property to disable the reload button. **/
    @Nullable
    protected ObservableBooleanValue getButtonReloadDisableProperty() {
        return null;
    }

    /** Create an icon with the given FA icon code. **/
    @Nonnull
    protected final Node createIconPane(@Nonnull final ToolConstants.ICONS icon) {
        final Label graphicPane = new Label(icon.code);
        graphicPane.getStyleClass().add(ToolConstants.CSS_CLASS_FONT_AWESOME);
        return graphicPane;
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
