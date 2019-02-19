/**
 * 
 */
package de.bcersows.photoimporter;

import com.google.inject.AbstractModule;

import de.bcersows.photoimporter.ui.UiController;

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
        bind(UiController.class).toInstance(new UiController());
    }
}
