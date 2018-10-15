package de.bcersows.photoimporter.ui;

import de.bcersows.photoimporter.Main;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Activity base class.
 * 
 * @author BCE
 */
public abstract class Activity {
    protected Main main = null;
    private Stage stage;
    private Scene scene;

    public Activity() {
        // empty
    }

    /** Initialize this activity. **/
    public abstract void initialize();

    /** Terminate this activity. **/
    public abstract void terminate();

    /** Run after activity was shown, e.g. to collect data. **/
    public abstract void postShow();

    /** Get the key of this activity. **/
    public abstract ActivityKey getActivityKey();

    /** Set the main. **/
    public final void setMain(final Main main) {
        this.main = main;
    }

    /** Set the stage of the application. **/
    public final void setStage(final Stage stage) {
        this.stage = stage;
    }

    /** Get the stage of the application. **/
    protected final Stage getStage() {
        return this.stage;
    }

    /**
     * @param scene
     */
    public void setScene(final Scene scene) {
        this.scene = scene;
    }

    protected Scene getScene() {
        return this.scene;
    }

    /**
     * Return the size as String.
     */
    protected final String getSize(final Region region) {
        return getSize(region.getWidth(), region.getHeight());
    }

    /**
     * Return the size of the stage as String.
     */
    protected final String getStageSize() {
        if (null != getStage()) {
            return getSize(getStage().getWidth(), getStage().getHeight());
        } else {
            return "";
        }
    }

    /**
     * Return the size of the scene as String.
     */
    protected final String getSceneSize() {
        if (null != getScene()) {
            return getSize(getScene().getWidth(), getScene().getHeight());
        } else {
            return "";
        }
    }

    /**
     * Return the size as String.
     */
    protected final String getSize(final double width, final double height) {
        return "(" + width + "/" + height + ")";
    }

    /** The possible activities. **/
    public enum ActivityKey {
        UI(UiController.class, "/fxml/Main.fxml");

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
