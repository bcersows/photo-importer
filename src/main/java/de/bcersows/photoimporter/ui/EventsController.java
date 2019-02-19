/**
 * 
 */
package de.bcersows.photoimporter.ui;

import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXListView;

import de.bcersows.photoimporter.ApplicationEventManager;
import de.bcersows.photoimporter.helper.FxPlatformHelper;
import de.bcersows.photoimporter.model.ApplicationEvent;
import de.bcersows.photoimporter.ui.components.ListCellFileFactory;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

    /** The cell factory for the file cells. **/
    private ListCellFileFactory listCellFileFactory;

    private final ObservableList<ApplicationEvent> eventsList;

    private final ListChangeListener<ApplicationEvent> eventsListener;

    @Inject
    public EventsController(final UiController uiController, final ApplicationEventManager eventManager) {
        super(uiController);

        this.eventManager = eventManager;
        this.eventsList = FXCollections.observableArrayList();
        this.eventsListener = evt -> {
            while (evt.next()) {
                this.eventsList.addAll(0, evt.getAddedSubList().stream().sorted(ApplicationEvent.getComparator()).collect(Collectors.toList()));
            }
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
        eventManager.getEvents().removeListener(eventsListener);
        this.eventsList.clear();
    }

    @Override
    public void postShowActivity() {
        this.eventsList.addAll(eventManager.getEvents().sorted(ApplicationEvent.getComparator()));
        eventManager.getEvents().addListener(eventsListener);
    }

    @Override
    protected Runnable getButtonReloadAction() {
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
