package de.bcersows.photoimporter.model;

public class FileInformation {
    /** The path of this cell. **/
    private final String path;
    private boolean wasCopied = false;

    public FileInformation(final String path) {
        super();
        this.path = path;
    }

    /** Flag this file as copied. **/
    public void setCopied() {
        this.wasCopied = true;
    }

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
