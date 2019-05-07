/**
 * 
 */
package de.bcersows.photoimporter;

import javax.annotation.Nonnull;

import com.google.inject.AbstractModule;

import de.bcersows.photoimporter.ui.UiController;

/**
 * @author BCE
 */
public class ApplicationConfig extends AbstractModule {
    private final ApplicationEventManager applicationEventManager;
    private final Main main;

    /** Initialize the application config, **/
    public ApplicationConfig(@Nonnull final ApplicationEventManager applicationEventManager, @Nonnull final Main main) {
        this.applicationEventManager = applicationEventManager;
        this.main = main;
    }

    @Override
    protected void configure() {
        bind(FileManager.class).toInstance(new FileManager());
        bind(ApplicationEventManager.class).toInstance(applicationEventManager);
        bind(UiController.class).toInstance(new UiController(main));
    }
}
