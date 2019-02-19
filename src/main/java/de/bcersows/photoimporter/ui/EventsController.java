/**
 * 
 */
package de.bcersows.photoimporter.ui;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXListView;

import de.bcersows.photoimporter.ApplicationEventManager;
import de.bcersows.photoimporter.helper.FxPlatformHelper;
import de.bcersows.photoimporter.model.ApplicationEvent;
import de.bcersows.photoimporter.ui.components.ListCellFileFactory;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;

/**
 * @author BCE
 */
public class EventsController extends Activity {
    private static final Logger LOG = LoggerFactory.getLogger(EventsController.class);

    @FXML
    private JFXListView<ApplicationEvent> events;

    /** Logging manager. **/
    private final ApplicationEventManager eventManager;

    /** Stores the files to update. **/
    private final ObservableMap<String, File> filesToUpdateMap = FXCollections.observableHashMap();

    /** The cell factory for the file cells. **/
    private ListCellFileFactory listCellFileFactory;

    private final ObservableList<ApplicationEvent> eventsList;

    private final ChangeListener<? super ObservableList<ApplicationEvent>> eventsListener;

    @Inject
    public EventsController(final UiController uiController, final ApplicationEventManager eventManager) {
        super(uiController);

        this.eventManager = eventManager;
        this.eventsList = FXCollections.observableArrayList();
        this.eventsListener = (oldVal, newVal, obs) -> {
            LOG.debug("Change! {}", newVal);
        };
    }

    @Override
    public void initialize() {
        // this.listCellFileFactory = new ListCellFileFactory(events);
        this.events.setItems(eventsList);

        FxPlatformHelper.runOnFxThread(() -> {
            this.events.refresh();
            this.events.requestLayout();
        });
    }

    @Override
    public void terminate() {
        this.filesToUpdateMap.clear();
        eventManager.getEvents().removeListener(eventsListener);
        this.eventsList.clear();
    }

    @Override
    public void postShowActivity() {
        this.eventsList.addAll(eventManager.getEvents());
        eventManager.getEvents().addListener(eventsListener);
    }

    @Override
    protected Runnable getButtonActionApply() {
        return null;
    }

    @Override
    protected Runnable getButtonActionRetry() {
        // return null;
        return () -> {
            eventManager.addEvent(new Date(), "Yeah! " + new Date());
        };
    }

    @Override
    public ActivityKey getActivityKey() {
        return ActivityKey.EVENTS;
    }
}
