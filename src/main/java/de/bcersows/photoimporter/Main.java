package de.bcersows.photoimporter;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

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

/**
 * Starts the importer, loads all other activities.
 * 
 * @author BCE
 */
public class Main extends Application {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    /** The name of the application. **/
    private static final String APPLICATION_NAME = "Photo Importer";
    /** The min height of the application. **/
    private static final double WINDOW_MIN_HEIGHT = 600;
    /** The min width of the application. **/
    private static final double WINDOW_MIN_WIDTH = 800;

    private ToolSettings settings;

    private final Map<ActivityKey, LoadedActivity> activities = new EnumMap<>(ActivityKey.class);
    private Activity currentActivity;

    private ApplicationEventManager applicationEventManager;

    // private Stage primaryStage ;
    private Scene scene;

    /** The Guice injector. **/
    // private Injector injector;

    @Override
    public void start(final Stage stage) throws Exception {
        this.applicationEventManager = new ApplicationEventManager();

        final Parameters parameters = getParameters();
        this.settings = new ToolSettings(parameters.getRaw().toArray(new String[0]));

        // this.primaryStage = stage;

        stage.setTitle(APPLICATION_NAME);
        stage.setOnCloseRequest(this::onCloseRequest);
        stage.setMinHeight(WINDOW_MIN_HEIGHT);
        stage.setMinWidth(WINDOW_MIN_WIDTH);
        // stage.initStyle(StageStyle.UNDECORATED);

        final Injector injector = Guice.createInjector(new ApplicationConfig(applicationEventManager));

        // load the activities
        loadActivities(injector);

        // show the first -- and only -- activity
        this.showActivity(ActivityKey.UI);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Load all known activities.
     * 
     * @param injector
     *            the injector used to load the activities
     **/
    private void loadActivities(final Injector injector) throws IOException {
        // load all activities
        for (final ActivityKey key : ActivityKey.values()) {
            final FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource(key.getFxmlPath()));
            final Class<? extends Activity> clazz = key.getActivityClass();
            final Activity activity = injector.getInstance(clazz);
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
        final Scene rootScene = getScene(activity.getRoot());

        this.currentActivity = activity.getActivity();
        rootScene.setRoot(activity.getRoot());
        this.currentActivity.postShow();
    }

    /** Get the scene or create a new one. **/
    private Scene getScene(final Parent root) {
        if (null == this.scene) {
            final Scene rootScene = new Scene(root);
            rootScene.getStylesheets().add(getClass().getResource("/style/application.css").toExternalForm());

            Font.loadFont(getClass().getResource("/fonts/fa-solid-900.ttf").toExternalForm(), 36);
            Font.loadFont(getClass().getResource("/fonts/Montserrat-Regular.ttf").toExternalForm(), 36);
            Font.loadFont(getClass().getResource("/fonts/Montserrat-Medium.ttf").toExternalForm(), 36);
            this.scene = rootScene;
        }

        return this.scene;
    }

    /** The close request, shutting down and clearing up everything. **/
    public void onCloseRequest(final WindowEvent windowEvent) {
        terminateCurrentActivity();
        this.applicationEventManager.shutdown();

        LOG.info("Shutting down!");

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
