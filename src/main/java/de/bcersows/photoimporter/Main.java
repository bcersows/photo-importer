package de.bcersows.photoimporter;

import java.util.HashMap;
import java.util.Map;

import de.bcersows.photoimporter.model.LoadedActivity;
import de.bcersows.photoimporter.model.ToolSettings;
import de.bcersows.photoimporter.ui.Activity;
import de.bcersows.photoimporter.ui.Activity.ActivityKey;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    private ToolSettings settings;

    private final Map<ActivityKey, LoadedActivity> activities = new HashMap<>();
    private Activity currentActivity = null;

    private Stage primaryStage = null;
    private Scene scene = null;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parameters parameters = getParameters();
        this.settings = new ToolSettings(parameters.getRaw().toArray(new String[0]));

        this.primaryStage = primaryStage;

        primaryStage.setTitle("Photo Importer");
        primaryStage.setOnCloseRequest(this::onCloseRequest);
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(660);
        // primaryStage.initStyle(StageStyle.UNDECORATED);

        // load the activities
        loadActivites();

        // show the first -- and only -- activity
        this.showActivity(ActivityKey.UI);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** Load all known activities. **/
    private void loadActivites() throws Exception {
        // load all activities
        for (final ActivityKey key : ActivityKey.values()) {
            final FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource(key.getFxmlPath()));
            final Class<? extends Activity> clazz = key.getActivityClass();
            final Activity activity = clazz.newInstance();
            activity.setMain(this);
            sceneLoader.setController(activity);
            final Parent root = sceneLoader.load();
            activity.initialize();

            final LoadedActivity loadedActivity = new LoadedActivity(key, key.getFxmlPath(), activity, root);

            this.activities.put(key, loadedActivity);
        }
    }

    /** Show an activity. **/
    private void showActivity(final ActivityKey key) {
        terminateCurrentActivity();

        final LoadedActivity activity = this.activities.get(key);
        final Scene scene = getScene(activity.getRoot());

        this.currentActivity = activity.getActivity();
        scene.setRoot(activity.getRoot());
        this.currentActivity.postShow();
    }

    /** Get the scene or create a new one. **/
    private Scene getScene(final Parent root) {
        if (null == this.scene) {
            final Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/application.css").toExternalForm());

            Font.loadFont(getClass().getResource("/fonts/fa-solid-900.ttf").toExternalForm(), 36);
            Font.loadFont(getClass().getResource("/fonts/Montserrat-Regular.ttf").toExternalForm(), 36);
            Font.loadFont(getClass().getResource("/fonts/Montserrat-Medium.ttf").toExternalForm(), 36);
            this.scene = scene;
        }

        return this.scene;
    }

    /** The close request. **/
    public void onCloseRequest(final WindowEvent windowEvent) {
        terminateCurrentActivity();
        System.out.println("Shutting down!");

        if (null != windowEvent) {
            windowEvent.consume();
        }

        System.exit(0);
    }

    /** Get the settings. Will be null before the JavaFX application started. **/
    public ToolSettings getSettings() {
        return this.settings;
    }

    /** Terminate the current activity, if any. **/
    private void terminateCurrentActivity() {
        if (null != this.currentActivity) {
            this.currentActivity.terminate();
            this.currentActivity = null;
        }
    }

    public static void main(final String[] args) {
        Application.launch(args);
    }

}
