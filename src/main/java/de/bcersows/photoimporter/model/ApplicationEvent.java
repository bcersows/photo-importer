/**
 * 
 */
package de.bcersows.photoimporter.model;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * @author BCE
 */
public class ApplicationEvent {
    /** The time of the event. **/
    private final Date time;
    /** The message of the event. **/
    private final String message;
    /** The type of the event. **/
    private final ApplicationEventType applicationEventType;

    /**
     * Create an event.
     */
    public ApplicationEvent(@Nonnull final Date time, @Nonnull final String message, @Nonnull final ApplicationEventType applicationEventType) {
        Objects.requireNonNull(time, "Must provide a time.");
        Objects.requireNonNull(message, "Must provide a message.");
        Objects.requireNonNull(applicationEventType, "Must provide a type.");

        this.time = time;
        this.message = message;
        this.applicationEventType = applicationEventType;
    }

    /**
     * Get the event time.
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * Get the event message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the type
     */
    public ApplicationEventType getApplicationEventType() {
        return this.applicationEventType;
    }

    @Override
    public String toString() {
        return "ApplicationEvent [time=" + this.time + ", message=" + this.message + ", type=" + this.applicationEventType + "]";
    }

}
