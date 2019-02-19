package de.bcersows.photoimporter.ui;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import de.bcersows.photoimporter.Main;
import de.bcersows.photoimporter.ToolConstants;
import de.bcersows.photoimporter.helper.FxPlatformHelper;
import de.bcersows.photoimporter.model.ToolSettings;
import de.bcersows.photoimporter.ui.Activity.ActivityKey;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller for the main UI.
 * 
 * @author BCE
 */
public class UiController {
    private static final Logger LOG = LoggerFactory.getLogger(UiController.class);

    @FXML
    private BorderPane rootContent;

    // nav area
    /** Just stores the nav entries temporarily to be able to use them with SceneBuilder. **/
    @FXML
    private Pane navStorage;
    @FXML
    private JFXDrawersStack drawersStack;
    @FXML
    private JFXDrawer navPanel;
    @FXML
    private VBox navPanelContent;
    @FXML
    private JFXHamburger hamburger;

    @FXML
    private VBox centerContent;

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
    private Button buttonNavMain;
    @FXML
    private Button buttonNavEvents;

    /** The main controller. **/
    private Main main;

    /** Action for the apply button. **/
    private final ObjectProperty<Runnable> buttonApplyAction;
    /** Action for the retry button. **/
    private final ObjectProperty<Runnable> buttonRetryAction;

    /** Create instance. **/
    public UiController() {
        buttonApplyAction = new SimpleObjectProperty<>();
        buttonRetryAction = new SimpleObjectProperty<>();
    }

    /** Initialize the main UI controller. **/
    @FXML
    public void initialize() {
        // initialize the hamburger menu
        this.navStorage.getChildren().remove(this.drawersStack);
        this.navStorage.getChildren().remove(this.navPanel);
        this.rootContent.getChildren().remove(centerContent);
        this.rootContent.setCenter(this.drawersStack);
        this.drawersStack.setContent(centerContent);
        this.rootContent.setLeft(null);
        this.navPanel.setSidePane(this.navPanelContent);

        final HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(this.hamburger);
        burgerTask.setRate(-1);
        this.hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            burgerTask.setRate(burgerTask.getRate() * -1);
            burgerTask.play();

            drawersStack.toggle(navPanel);
        });

        // set the texts
        this.buttonApplyIcon.setText(ToolConstants.ICONS.FA_COPY.code);
        this.buttonReloadIcon.setText(ToolConstants.ICONS.FA_REPEAT.code);
        this.buttonExitIcon.setText(ToolConstants.ICONS.FA_EXIT.code);

        this.buttonApply.visibleProperty().bind(this.buttonApplyAction.isNotNull());
        this.buttonApply.managedProperty().bind(this.buttonApply.visibleProperty());
        this.buttonReload.visibleProperty().bind(this.buttonRetryAction.isNotNull());
        this.buttonReload.managedProperty().bind(this.buttonReload.visibleProperty());

        this.buttonNavMain.setOnAction(event -> this.main.showActivity(ActivityKey.IMPORT));
        this.buttonNavEvents.setOnAction(event -> this.main.showActivity(ActivityKey.EVENTS));
    }

    @FXML
    private void onActionButtonReload(final ActionEvent actionEvent) {
        runAction(this.buttonRetryAction.get());
    }

    @FXML
    private void onActionButtonApply(final ActionEvent actionEvent) {
        runAction(this.buttonApplyAction.get());
    }

    /** Run the given action, if exists. **/
    private void runAction(@Nullable final Runnable action) {
        if (null != action) {
            action.run();
        }
    }

    @FXML
    private void onActionButtonExit(final ActionEvent actionEvent) {
        this.main.onCloseRequest(null);
    }

    /**
     * @param main
     */
    public void setMain(final Main main) {
        this.main = main;
    }

    /** Get the settings for the application. **/
    public final ToolSettings getApplicationSettings() {
        return this.main.getSettings();
    }

    /**
     * Set the actions for the main buttons.
     * 
     * @param applyRunnable
     *            action for apply button
     * @param retryRunnable
     *            action for retry button
     */
    public final void setButtonActions(@Nullable final Runnable applyRunnable, @Nullable final Runnable retryRunnable) {
        this.buttonApplyAction.set(applyRunnable);
        this.buttonRetryAction.set(retryRunnable);
    }

    /**
     * Set the dynamic content to the given node.
     * 
     * @param root
     */
    public void setContent(final Parent root) {
        FxPlatformHelper.runOnFxThread(() -> {
            this.centerContent.getChildren().clear();
            VBox.setVgrow(root, Priority.ALWAYS);
            this.centerContent.getChildren().add(root);
        });
    }

}
