/**
 * 
 */
package de.bcersows.photoimporter;

import java.util.Date;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bcersows.photoimporter.model.ApplicationEvent;
import de.bcersows.photoimporter.model.ApplicationEventType;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;

/**
 * Manager for logging events for the user.
 * 
 * @author BCE
 */
public class ApplicationEventManager {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventManager.class);
    /** Gathered events of this run. **/
    private final ReadOnlyListWrapper<ApplicationEvent> events;

    /** Create the manager. **/
    public ApplicationEventManager() {
        this.events = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    }

    /**
     * Add an event
     * 
     * @param time
     *            of the event
     * @param message
     *            of the event
     */
    public void addEvent(@Nonnull final Date time, @Nonnull final String message) {
        addEvent(time, message, ApplicationEventType.GENERAL);

    }

    /**
     * Add an event.
     * 
     * @param time
     *            of the event
     * @param message
     *            of the event
     * @param applicationEventType
     *            event type
     */
    public void addEvent(final @Nonnull Date time, final @Nonnull String message, @Nonnull final ApplicationEventType applicationEventType) {
        this.events.add(new ApplicationEvent(time, message, applicationEventType));
    }

    /** Return the collected events. **/
    public ReadOnlyListProperty<ApplicationEvent> getEvents() {
        return this.events.getReadOnlyProperty();
    }

    /** Shuts down the event manager. **/
    public void shutdown() {
        this.events.clear();
    }

}
