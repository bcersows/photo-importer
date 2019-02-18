/**
 * 
 */
package de.bcersows.photoimporter;

import com.google.inject.AbstractModule;

/**
 * @author BCE
 */
public class ApplicationConfig extends AbstractModule {
    private final ApplicationEventManager applicationEventManager;

    public ApplicationConfig(final ApplicationEventManager applicationEventManager) {
        this.applicationEventManager = applicationEventManager;
    }

    @Override
    protected void configure() {
        bind(FileManager.class).toInstance(new FileManager());
        bind(ApplicationEventManager.class).toInstance(applicationEventManager);
    }
}
