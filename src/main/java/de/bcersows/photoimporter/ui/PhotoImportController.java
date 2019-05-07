/**
 * 
 */
package de.bcersows.photoimporter.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;

import de.bcersows.photoimporter.ApplicationEventManager;
import de.bcersows.photoimporter.FileManager;
import de.bcersows.photoimporter.ToolConstants;
import de.bcersows.photoimporter.helper.CustomNamedThreadFactory;
import de.bcersows.photoimporter.helper.FxPlatformHelper;
import de.bcersows.photoimporter.model.ApplicationEventType;
import de.bcersows.photoimporter.model.CopyInformation;
import de.bcersows.photoimporter.model.FileException;
import de.bcersows.photoimporter.model.FileInformation;
import de.bcersows.photoimporter.model.ToolSettings;
import de.bcersows.photoimporter.texts.TextDefinition;
import de.bcersows.photoimporter.ui.components.ListCellFileFactory;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * @author BCE
 */
public class PhotoImportController extends Activity {
    private static final Logger LOG = LoggerFactory.getLogger(PhotoImportController.class);

    /** Format for the progress time. **/
    private static final String PROGRESS_DATE_FORMAT = "HH:mm:ss':' ";
    @FXML
    private Pane headerLoadingIndicator;
    @FXML
    private HBox areaProgress;
    @FXML
    private Label labelProgressStatus;

    @FXML
    private SplitPane contentArea;

    @FXML
    private Label labelConfigurationSource;
    @FXML
    private Label labelConfigurationDestination;

    @FXML
    private Label labelFileAmountSource;
    @FXML
    private Label labelFileAmountDestination;
    @FXML
    private Label labelFileAmountUpdate;

    @FXML
    private VBox contentSide;
    @FXML
    private VBox listFilesArea;
    @FXML
    private JFXScrollPane listFilesScroll;
    @FXML
    private JFXListView<FileInformation> listFilesToUpdate;

    @FXML
    private Label buttonApplyIcon;
    @FXML
    private Label buttonReloadIcon;
    @FXML
    private Label buttonExitIcon;

    @FXML
    private Button buttonSortAsc;
    @FXML
    private Button buttonSortDesc;

    private final StringProperty progressProperty = new SimpleStringProperty();

    /** Manager for file-based operations. **/
    private final FileManager fileManager;
    /** Logging manager. **/
    private final ApplicationEventManager eventManager;

    /** Store the found source files. **/
    private final Map<String, File> sourceFiles = new HashMap<>();
    /** Store the found destination files. **/
    private final Map<String, File> destinationFiles = new HashMap<>();
    /** Stores the files to update. **/
    private final ObservableMap<String, File> filesToUpdateMap = FXCollections.observableHashMap();

    /** The source paths for the copy process. **/
    private List<String> sourcePaths;
    /** The destination path for the copy process. **/
    private String destinationPath;

    /** Executor service. **/
    private ScheduledExecutorService executor;
    /** If it is currently loading. **/
    private final BooleanProperty loadingInProgress = new SimpleBooleanProperty(false);

    /** The cell factory for the file cells. **/
    private ListCellFileFactory listCellFileFactory;
    private final DateFormat stateProgressDateFormat;

    @Inject
    public PhotoImportController(@Nonnull final UiController uiController, @Nonnull final FileManager fileManager,
            @Nonnull final ApplicationEventManager eventManager) {
        super(uiController);

        this.fileManager = fileManager;
        this.eventManager = eventManager;

        this.stateProgressDateFormat = new SimpleDateFormat(PROGRESS_DATE_FORMAT);
    }

    @Override
    public void initialize() {
        this.areaProgress.visibleProperty().bind(this.progressProperty.isNotEmpty());
        this.areaProgress.managedProperty().bind(this.areaProgress.visibleProperty());
        this.headerLoadingIndicator.visibleProperty().bind(this.loadingInProgress);
        this.headerLoadingIndicator.managedProperty().bind(this.headerLoadingIndicator.visibleProperty());

        this.labelProgressStatus.textProperty().bind(this.progressProperty);

        this.executor = Executors.newSingleThreadScheduledExecutor(new CustomNamedThreadFactory("PHOTO_IMPORT_LOAD"));

        this.listCellFileFactory = new ListCellFileFactory(listFilesToUpdate);

        FxPlatformHelper.runOnFxThread(() -> {
            this.listFilesToUpdate.refresh();
            this.listFilesToUpdate.requestLayout();
        });

        // set the icons of the sort buttons
        this.buttonSortAsc.setGraphic(createIconPane(ToolConstants.ICONS.FA_SORT_ASC));
        this.buttonSortDesc.setGraphic(createIconPane(ToolConstants.ICONS.FA_SORT_DESC));
        this.buttonSortAsc.setOnAction(event -> listCellFileFactory.sortItems(true));
        this.buttonSortDesc.setOnAction(event -> listCellFileFactory.sortItems(false));
    }

    @Override
    public void terminate() {
        this.sourceFiles.clear();
        this.destinationFiles.clear();
        this.filesToUpdateMap.clear();
    }

    @Override
    public void postShowActivity() {
        loadFiles();

        final ToolSettings settings = getApplicationSettings();
        this.sourcePaths = settings.getSourceFolder();
        this.destinationPath = settings.getDestinationFolder();

        this.labelConfigurationSource.setText(String.join(", ", sourcePaths));
        this.labelConfigurationDestination.setText(destinationPath);
    }

    /** Load the files from the disk. **/
    private void loadFiles() {
        synchronized (loadingInProgress) {
            if (!this.loadingInProgress.get()) {
                this.loadingInProgress.set(true);
            } else {
                return;
            }
        }

        // define the task
        final Task<Void> fileLoadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateStateProgress("Start to load files.");
                sourceFiles.clear();
                destinationFiles.clear();
                filesToUpdateMap.clear();
                try {
                    updateStateProgress("Searching source files...");
                    sourceFiles.putAll(fileManager.findFiles(sourcePaths.toArray(new String[0])));
                    updateStateProgress("Found " + sourceFiles.size() + " files on source. Searching destination files...");
                    destinationFiles.putAll(fileManager.findFiles(destinationPath));
                    updateStateProgress("Found " + destinationFiles.size() + " files on destination. Calculating difference...");
                } catch (final FileException e) {
                    sourceFiles.clear();
                    destinationFiles.clear();

                    throw e;
                }

                // map the files...
                filesToUpdateMap.putAll(sourceFiles.entrySet().parallelStream().filter(entry -> !destinationFiles.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
                // ... and set them to the list
                FxPlatformHelper.runOnFxThread(
                        () -> listCellFileFactory.setItems(filesToUpdateMap.values().parallelStream().map(FileInformation::new).collect(Collectors.toList())));

                return null;
            }
        };

        fileLoadTask.setOnCancelled(event -> {
            LOG.info("Execution cancelled.");
            this.loadingInProgress.set(false);
            updateStateProgress("File loading cancelled.");
            FxPlatformHelper.runOnFxThread(listCellFileFactory::clear);
        });
        fileLoadTask.setOnFailed(event -> {
            LOG.warn("Task failed: {}", fileLoadTask.getException().getMessage());
            this.loadingInProgress.set(false);
            updateStateProgress("File loading failed: " + fileLoadTask.getException().getMessage());
            FxPlatformHelper.runOnFxThread(listCellFileFactory::clear);
        });
        fileLoadTask.setOnSucceeded(event -> {
            LOG.info("Source Files: {}", sourceFiles);
            LOG.info("Destination Files: {}", destinationFiles);
            final String message = "Finished loading. Found " + sourceFiles.size() + " source and " + destinationFiles.size() + " destination files.";
            Platform.runLater(() -> {
                LOG.info(message);

                labelFileAmountSource.setText(sourceFiles.size() + TextDefinition.EMPTY);
                labelFileAmountDestination.setText(destinationFiles.size() + TextDefinition.EMPTY);
                labelFileAmountUpdate.setText(filesToUpdateMap.size() + TextDefinition.EMPTY);
            });
            this.loadingInProgress.set(false);
            updateStateProgress(message);
        });

        // start the task
        this.executor.submit(fileLoadTask);
    }

    /** Copy the found files in a task. **/
    private void copyFiles() {
        synchronized (loadingInProgress) {
            if (!this.loadingInProgress.get()) {
                this.loadingInProgress.set(true);
            } else {
                return;
            }
        }

        // define the task
        final Task<Void> copyTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Consumer<CopyInformation> statusUpdateCallback = status -> {
                    updateStateProgress(status.getMessage());
                    listCellFileFactory.updateCopyState(status.getFilePath(), status.isCopySuccess());
                };

                updateStateProgress("Start copying " + filesToUpdateMap.size() + " files.");
                final int copied = fileManager.copyFiles(filesToUpdateMap, destinationPath, statusUpdateCallback);
                LOG.info("Copied: #{}", copied);

                return null;
            }
        };

        copyTask.setOnCancelled(event -> {
            LOG.info("Execution cancelled.");
            this.loadingInProgress.set(false);
            updateStateProgress("File copying cancelled.");
        });
        copyTask.setOnFailed(event -> {
            LOG.warn("Task failed: {}", copyTask.getException().getMessage());
            this.loadingInProgress.set(false);
            updateStateProgress("File copying failed: " + copyTask.getException().getMessage());
        });
        copyTask.setOnSucceeded(event -> {
            this.loadingInProgress.set(false);

            // directly reload the list
            loadFiles();
        });

        // start the task
        this.executor.submit(copyTask);
    }

    @Override
    protected Runnable getButtonApplyAction() {
        return () -> {
            progressProperty.set(null);
            copyFiles();
        };
    }

    @Override
    protected Runnable getButtonReloadAction() {
        return this::loadFiles;
    }

    @Override
    protected BooleanBinding getButtonApplyDisableProperty() {
        return this.loadingInProgress.not().not();
    }

    @Override
    protected BooleanBinding getButtonReloadDisableProperty() {
        return this.loadingInProgress;
        // return this.loadingInProgress.or(Bindings.size(filesToUpdateMap).lessThanOrEqualTo(0));
    }

    /** Set the progress property to the given message. **/
    private void updateStateProgress(final String message) {
        // can only set it when not bound already
        if (!progressProperty.isBound()) {
            final String finalMessage = stateProgressDateFormat.format(new Date()) + message;
            eventManager.addEvent(new Date(), message, ApplicationEventType.COPY);
            FxPlatformHelper.runOnFxThread(() -> progressProperty.set(finalMessage));
        } else {
            LOG.warn("It was intended to write a progress message ({}), but it is currently bound otherwise.", message);
        }
    }

    @Override
    public ActivityKey getActivityKey() {
        return ActivityKey.IMPORT;
    }
}
