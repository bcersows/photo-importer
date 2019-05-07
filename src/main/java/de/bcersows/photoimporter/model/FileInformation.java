package de.bcersows.photoimporter.model;

import java.io.File;

import javax.annotation.Nonnull;

/**
 * Information about a single file.
 * 
 * @author BCE
 */
public class FileInformation {
    /** The path of this cell. **/
    private final String path;
    private boolean wasCopied = false;

    public FileInformation(@Nonnull final File file) {
        this.path = file.getAbsolutePath();
    }

    /** Flag this file as copied. **/
    public void setCopied() {
        this.wasCopied = true;
    }

    @Nonnull
    public String getPath() {
        return path;
    }

    public boolean isWasCopied() {
        return wasCopied;
    }

    public void setWasCopied(final boolean wasCopied) {
        this.wasCopied = wasCopied;
    }

    @Override
    public String toString() {
        return "FileInformation [path=" + path + ", wasCopied=" + wasCopied + "]";
    }

}
