/**
 * 
 */
package de.bcersows.photoimporter;

import com.google.inject.AbstractModule;

/**
 * @author BCE
 */
public class ApplicationConfig extends AbstractModule {
    public ApplicationConfig() {
        // nothing
    }

    @Override
    protected void configure() {
        bind(FileManager.class).toInstance(new FileManager());
        bind(ApplicationEventManager.class).toInstance(new ApplicationEventManager());
    }
}
