package de.bcersows.photoimporter.ui;

import de.bcersows.photoimporter.Main;

/**
 * Activity base class.
 * 
 * @author BCE
 */
public abstract class Activity {
    protected Main main = null;

    public Activity() {
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
    public void setMain(final Main main) {
        this.main = main;
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
