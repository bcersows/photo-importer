package de.bcersows.photoimporter.ui;

import javax.annotation.Nonnull;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
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
    private Label title;

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
    private final Main main;

    /** Action for the apply button. **/
    private final ObjectProperty<Runnable> buttonApplyAction;
    /** Action for the reload button. **/
    private final ObjectProperty<Runnable> buttonReloadAction;
    /** If the apply button is disabled. **/
    private final BooleanProperty buttonApplyDisabledProperty;
    /** If the reload button is disabled. **/
    private final BooleanProperty buttonReloadDisabledProperty;

    /** Create instance. **/
    public UiController(@Nonnull final Main main) {
        this.main = main;

        this.buttonApplyAction = new SimpleObjectProperty<>();
        this.buttonReloadAction = new SimpleObjectProperty<>();

        this.buttonApplyDisabledProperty = new SimpleBooleanProperty();
        this.buttonReloadDisabledProperty = new SimpleBooleanProperty();
    }

    /** Initialize the main UI controller. **/
    @FXML
    public void initialize() {
        // initialize the side panel
        this.navStorage.getChildren().remove(this.drawersStack);
        this.navStorage.getChildren().remove(this.navPanel);
        this.rootContent.getChildren().remove(centerContent);
        this.rootContent.setCenter(this.drawersStack);
        this.drawersStack.setContent(centerContent);
        this.rootContent.setLeft(null);
        this.navPanel.setSidePane(this.navPanelContent);

        // initialize the hamberder menu
        final HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(this.hamburger);
        burgerTask.setRate(-1);
        this.hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> drawersStack.toggle(navPanel));
        final Runnable toggleBurger = () -> {
            burgerTask.setRate(burgerTask.getRate() * -1);
            burgerTask.play();
        };
        this.navPanel.setOnDrawerClosing(evt -> toggleBurger.run());
        this.navPanel.setOnDrawerOpening(evt -> toggleBurger.run());

        // set the texts
        this.buttonApplyIcon.setText(ToolConstants.ICONS.FA_COPY.code);
        this.buttonReloadIcon.setText(ToolConstants.ICONS.FA_REPEAT.code);
        this.buttonExitIcon.setText(ToolConstants.ICONS.FA_EXIT.code);

        // bind button visibility and disable
        this.buttonApply.visibleProperty().bind(this.buttonApplyAction.isNotNull());
        this.buttonApply.managedProperty().bind(this.buttonApply.visibleProperty());
        this.buttonReload.visibleProperty().bind(this.buttonReloadAction.isNotNull());
        this.buttonReload.managedProperty().bind(this.buttonReload.visibleProperty());

        // disable the buttons depending on the properties
        this.buttonApply.disableProperty().bind(this.buttonApplyDisabledProperty);
        this.buttonReload.disableProperty().bind(this.buttonReloadDisabledProperty);

        // add the handlers for the page swap actions
        this.title.setOnMouseClicked(event -> this.main.showActivity(ActivityKey.IMPORT));
        this.buttonNavMain.setOnAction(event -> this.main.showActivity(ActivityKey.IMPORT));
        this.buttonNavEvents.setOnAction(event -> this.main.showActivity(ActivityKey.EVENTS));
    }

    /** Action handler for the reload button. **/
    @FXML
    private void onActionButtonReload(final ActionEvent actionEvent) {
        LOG.trace(Activity.LOG_MESSAGE_EVENT, actionEvent);

        runAction(this.buttonReloadAction.get());
    }

    /** Action handler for the apply button. **/
    @FXML
    private void onActionButtonApply(final ActionEvent actionEvent) {
        LOG.trace(Activity.LOG_MESSAGE_EVENT, actionEvent);

        runAction(this.buttonApplyAction.get());
    }

    /** Run the given action, if it exists. **/
    private void runAction(@Nullable final Runnable action) {
        if (null != action) {
            action.run();
        }
    }

    @FXML
    private void onActionButtonExit(final ActionEvent actionEvent) {
        this.main.onCloseRequest(null);
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
     * @param reloadRunnable
     *            action for reload button
     */
    public final void setButtonActions(@Nullable final Runnable applyRunnable, @Nullable final Runnable reloadRunnable) {
        this.buttonApplyAction.set(applyRunnable);
        this.buttonReloadAction.set(reloadRunnable);
    }

    /**
     * Set the button disable properties.
     * 
     * @param buttonApplyDisable
     *            binding to disable the apply button
     * @param buttonReloadDisable
     *            binding to disable the reload button
     */
    public void setButtonDisableProperties(@Nullable final ObservableBooleanValue buttonApplyDisable,
            @Nullable final ObservableBooleanValue buttonReloadDisable) {
        // unbind existing properties
        this.buttonApplyDisabledProperty.unbind();
        this.buttonReloadDisabledProperty.unbind();

        // bind or set the new bindings
        setButtonDisableProperty(this.buttonApplyDisabledProperty, buttonApplyDisable);
        setButtonDisableProperty(this.buttonReloadDisabledProperty, buttonReloadDisable);
    }

    /**
     * Set the dynamic content to the given node.
     */
    public void setContent(final Parent root) {
        FxPlatformHelper.runOnFxThread(() -> {
            this.centerContent.getChildren().clear();
            VBox.setVgrow(root, Priority.ALWAYS);
            this.centerContent.getChildren().add(root);
        });
    }

    /**
     * Set the disable property of a given property with the given binding.
     */
    private void setButtonDisableProperty(@Nonnull final BooleanProperty buttonDisabledProperty, @Nullable final ObservableBooleanValue buttonDisableBinding) {
        buttonDisabledProperty.unbind();

        // set the disable binding
        if (null != buttonDisableBinding) {
            buttonDisabledProperty.bind(buttonDisableBinding);
        } else {
            buttonDisabledProperty.set(false);
        }
    }

}
