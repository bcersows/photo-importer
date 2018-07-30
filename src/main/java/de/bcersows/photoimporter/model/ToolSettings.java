package de.bcersows.photoimporter.model;

import java.util.LinkedList;
import java.util.List;

/**
 * The settings for the application.
 * 
 * @author BCE
 */
public class ToolSettings {
    /** The destination folder. **/
    private String destinationFolder;
    /** The source folder. **/
    private List<String> sourceFolder;

    /**
     * 
     * @param parameters
     *            list of parameters. Must contain at least two elements.
     */
    public ToolSettings(final String... parameters) {
        super();

        if (parameters.length < 2) {
            throw new IllegalStateException("Must specify at least two arguments.");
        }
        destinationFolder = parameters[0];

        // collect the source folders
        this.sourceFolder = new LinkedList<>();
        for (int i = 1; i < parameters.length; i++) {
            sourceFolder.add(parameters[i]);
        }
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public List<String> getSourceFolder() {
        return sourceFolder;
    }

    public void setDestinationFolder(final String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public void setSourceFolder(final List<String> sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    @Override
    public String toString() {
        return "ToolSettings [destinationFolder=" + destinationFolder + ", sourceFolder=" + sourceFolder + "]";
    }

}
