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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import de.bcersows.photoimporter.FileManager;
import de.bcersows.photoimporter.ToolConstants;
import de.bcersows.photoimporter.helper.FxPlatformHelper;
import de.bcersows.photoimporter.model.CopyInformation;
import de.bcersows.photoimporter.model.FileException;
import de.bcersows.photoimporter.model.FileInformation;
import de.bcersows.photoimporter.model.ToolSettings;
import de.bcersows.photoimporter.ui.components.ListCellFileFactory;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Controller for the UI.
 * 
 * @author BCE
 */
public class UiController extends Activity {
    private static final Logger LOG = Logger.getLogger(UiController.class.getName());

    @FXML
    private BorderPane rootContent;

    @FXML
    private Pane headerLoadingIndicator;
    @FXML
    private HBox areaProgress;
    @FXML
    private Label labelProgressStatus;

    @FXML
    private VBox centerContent;
    // @FXML
    // private ScrollPane contentAreaScroll;
    @FXML
    private BorderPane contentArea;

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
    private Button buttonApply;
    @FXML
    private Button buttonReload;
    @FXML
    private Label buttonApplyIcon;
    @FXML
    private Label buttonReloadIcon;
    @FXML
    private Label buttonExitIcon;

    @FXML
    private JFXHamburger hamburger;

    private final StringProperty progressProperty = new SimpleStringProperty();

    private final FileManager fileManager;
    /** Store the found source files. **/
    private final Map<String, File> sourceFiles = new HashMap<>();
    /** Store the found destination files. **/
    private final Map<String, File> destinationFiles = new HashMap<>();
    /** Also store the files to update. **/
    private final ObservableMap<String, File> filesToUpdateMap = FXCollections.observableHashMap();

    private List<String> sourcePaths;
    private String destinationPath;

    private ScheduledExecutorService executor;
    private final BooleanProperty loadingInProgress = new SimpleBooleanProperty(false);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    /** The cell factory for the file cells. **/
    private ListCellFileFactory listCellFileFactory;
    private final DateFormat stateProgressDateFormat;

    private final ChangeListener<Number> contentResizeListener = (obs, oldVal, newVal) -> {
        // LOG.warning("Something got resized: stage=" + getStageSize() + ", listFilesScroll=" + getSize(this.listFilesScroll) + ", listFilesToUpdate="
        // + getSize(this.listFilesToUpdate) + ".");
        System.out.println("Something got resized: stage=" + getStageSize() + ", scene=" + getSceneSize() + ", listFilesArea=" + getSize(this.listFilesArea));
        // System.out.println("Detected by " + obs);

        calculateContentSize();
    };

    public UiController() {
        this.fileManager = new FileManager();

        this.stateProgressDateFormat = new SimpleDateFormat("HH:mm:ss':' ");
    }

    /**
     * Calculate and set the size of the content.
     */
    private synchronized void calculateContentSize() {
        // TODO bce bce calculate size
        // final double spacing = 10;
        final double stageWidth = getStage().getWidth();
        final double stageHeight = getStage().getHeight();

        final double sceneWidth = getScene().getWidth();

        final double parentWidth = getWidth(this.contentArea);
        final double sidebarWidth = this.contentSide.getWidth();
        final double containerWidth = getWidth(this.listFilesArea);
        final double containerHeight = this.contentArea.getHeight() - this.contentArea.getPadding().getTop() - this.contentArea.getPadding().getBottom();

        double width = parentWidth - sidebarWidth;
        if (width > stageWidth) {
            width = stageWidth - sidebarWidth;
        }

        final double height = containerHeight;

        final double finalWidth = width;

        LOG.warning("Calculated new size: " + finalWidth + "/" + height);

        System.out.println("Inside area width: " + getWidth(this.contentArea));

        Platform.runLater(() -> {
            // this.listFilesArea.setPrefWidth(finalWidth);
            // this.listFilesArea.setMaxWidth(finalWidth);
            // this.listFilesArea.setPrefHeight(height);
            // this.listFilesArea.setMaxHeight(height);

            System.out.println("Run later area width: " + getWidth(this.contentArea));
        });
    }

    /**
     * @param region
     * @return
     */
    private double getWidth(final Region region) {
        return region.getWidth() - region.getPadding().getLeft() - region.getPadding().getRight();
    }

    @FXML
    private void onActionButtonReload(final ActionEvent actionEvent) {
        loadFiles();
    }

    @FXML
    private void onActionButtonApply(final ActionEvent actionEvent) {
        progressProperty.set(null);

        copyFiles();
    }

    @FXML
    private void onActionButtonExit(final ActionEvent actionEvent) {
        this.main.onCloseRequest(null);
    }

    @Override
    public void initialize() {
        if (this.initialized.compareAndSet(false, true)) {
            this.areaProgress.visibleProperty().bind(this.progressProperty.isNotEmpty());
            this.areaProgress.managedProperty().bind(this.areaProgress.visibleProperty());
            this.headerLoadingIndicator.visibleProperty().bind(this.loadingInProgress);
            this.headerLoadingIndicator.managedProperty().bind(this.headerLoadingIndicator.visibleProperty());

            this.buttonReload.disableProperty().bind(this.loadingInProgress);
            this.buttonApply.disableProperty().bind(this.loadingInProgress.or(Bindings.size(filesToUpdateMap).lessThanOrEqualTo(0)));

            this.labelProgressStatus.textProperty().bind(this.progressProperty);

            this.executor = Executors.newSingleThreadScheduledExecutor();

            this.listCellFileFactory = new ListCellFileFactory(listFilesToUpdate);

            this.buttonApplyIcon.setText(ToolConstants.ICONS.FA_COPY.code);
            this.buttonReloadIcon.setText(ToolConstants.ICONS.FA_REPEAT.code);
            this.buttonExitIcon.setText(ToolConstants.ICONS.FA_EXIT.code);

            // initialize the hamburger menu
            final JFXDrawer drawer = new JFXDrawer();
            drawer.setDefaultDrawerSize(150);

            final JFXDrawersStack drawerStack = new JFXDrawersStack();
            // drawerStack.setContent(this.rootContent);

            final HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(this.hamburger);
            burgerTask.setRate(-1);
            this.hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
                burgerTask.setRate(burgerTask.getRate() * -1);
                burgerTask.play();

                drawerStack.toggle(drawer);
            });

            Platform.runLater(() -> {
                this.listFilesToUpdate.refresh();
                this.listFilesToUpdate.requestLayout();
            });
        }
    }

    @Override
    public void terminate() {
        configureResizeListener(false);

        this.sourceFiles.clear();
        this.destinationFiles.clear();
        this.filesToUpdateMap.clear();
    }

    @Override
    public void postShow() {
        configureResizeListener(true);

        loadFiles();
        // Platform.runLater(() -> {
        // contentAreaScroll.setVvalue(0);
        // });

        // TODO bce bce get from Main
        final ToolSettings settings = this.main.getSettings();
        this.sourcePaths = settings.getSourceFolder();
        this.destinationPath = settings.getDestinationFolder();
    }

    /** Load the files from the disk. **/
    private void loadFiles() {
        synchronized (loadingInProgress) {
            if (false == this.loadingInProgress.get()) {
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

                filesToUpdateMap.putAll(sourceFiles.entrySet().parallelStream().filter(entry -> {
                    return !destinationFiles.containsKey(entry.getKey());
                }).collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
                Platform.runLater(() -> {
                    listCellFileFactory.setItems(
                            filesToUpdateMap.values().parallelStream().map(file -> new FileInformation(file.getAbsolutePath())).collect(Collectors.toList()));
                    calculateContentSize();
                });

                return null;
            }
        };

        fileLoadTask.setOnCancelled(event -> {
            System.out.println("Execution cancelled.");
            this.loadingInProgress.set(false);
            updateStateProgress("File loading cancelled.");
            FxPlatformHelper.runOnFxThread(() -> {
                this.listCellFileFactory.clear();
            });
        });
        fileLoadTask.setOnFailed(event -> {
            System.err.println("Task failed: " + fileLoadTask.getException().getMessage());
            fileLoadTask.getException().printStackTrace();
            this.loadingInProgress.set(false);
            updateStateProgress("File loading failed: " + fileLoadTask.getException().getMessage());
            FxPlatformHelper.runOnFxThread(() -> {
                this.listCellFileFactory.clear();
            });
        });
        fileLoadTask.setOnSucceeded(event -> {
            LOG.info("Source Files: " + sourceFiles);
            LOG.info("Destination Files: " + destinationFiles);
            final String message = "Finished loading. Found " + sourceFiles.size() + " source and " + destinationFiles.size() + " destination files.";
            Platform.runLater(() -> {
                LOG.info(message);

                labelFileAmountSource.setText(sourceFiles.size() + "");
                labelFileAmountDestination.setText(destinationFiles.size() + "");
                labelFileAmountUpdate.setText(filesToUpdateMap.size() + "");
            });
            this.loadingInProgress.set(false);
            updateStateProgress(message);
        });

        // start the task
        this.executor.submit(fileLoadTask);

        // Platform.runLater(() -> {
        // contentAreaScroll.setVvalue(0);
        // });
    }

    /** Copy the found files in a task. **/
    private void copyFiles() {
        synchronized (loadingInProgress) {
            if (false == this.loadingInProgress.get()) {
                this.loadingInProgress.set(true);
            } else {
                return;
            }
        }

        // define the task
        final Task<Void> copyTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Callback<CopyInformation, Void> statusUpdateCallback = new Callback<CopyInformation, Void>() {
                    @Override
                    public Void call(final CopyInformation status) {
                        updateStateProgress(status.getMessage());
                        listCellFileFactory.updateCopyState(status.getFilePath(), status.isCopySuccess());
                        return null;
                    }
                };

                updateStateProgress("Start copying " + filesToUpdateMap.size() + " files.");
                final int copied = fileManager.copyFiles(filesToUpdateMap, destinationPath, statusUpdateCallback);
                LOG.info("Copied: " + copied);

                return null;
            }
        };

        copyTask.setOnCancelled(event -> {
            LOG.info("Execution cancelled.");
            this.loadingInProgress.set(false);
            updateStateProgress("File copying cancelled.");
        });
        copyTask.setOnFailed(event -> {
            LOG.warning("Task failed: " + copyTask.getException().getMessage());
            copyTask.getException().printStackTrace();
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

    /** Set the progress property to the given message. **/
    private void updateStateProgress(final String message) {
        if (false == progressProperty.isBound()) {
            Platform.runLater(() -> {
                progressProperty.set(stateProgressDateFormat.format(new Date()) + message);
            });
        } else {
            LOG.warning("It was intended to write a progress message (" + message + "), but it is currently bound otherwise.");
        }
    }

    @Override
    public ActivityKey getActivityKey() {
        return ActivityKey.UI;
    }

    /**
     * Configure or remove the resize listener, depending on the parameter.
     */
    private void configureResizeListener(final boolean add) {
        // TODO bce bce configure listener
        if (add) {
            getStage().heightProperty().addListener(contentResizeListener);
            getStage().widthProperty().addListener(contentResizeListener);
            // this.listFilesScroll.heightProperty().addListener(contentResizeListener);
            // this.listFilesScroll.widthProperty().addListener(contentResizeListener);
            // this.listFilesToUpdate.heightProperty().addListener(contentResizeListener);
            // this.listFilesToUpdate.widthProperty().addListener(contentResizeListener);
        } else {
            getStage().heightProperty().removeListener(contentResizeListener);
            getStage().widthProperty().removeListener(contentResizeListener);
            this.listFilesScroll.heightProperty().removeListener(contentResizeListener);
            this.listFilesScroll.widthProperty().removeListener(contentResizeListener);
            this.listFilesToUpdate.heightProperty().removeListener(contentResizeListener);
            this.listFilesToUpdate.widthProperty().removeListener(contentResizeListener);
        }
    }
}
