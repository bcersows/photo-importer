package de.bcersows.photoimporter.model;

/**
 * Information about the copy progress. Contains the file name, copy success and the progress message.
 * 
 * @author BCE
 */
public class CopyInformation {
    private final String filePath;
    private final boolean copySuccess;
    private final String message;

    public CopyInformation(final String filePath, final boolean copySuccess, final String message) {
        super();
        this.filePath = filePath;
        this.copySuccess = copySuccess;
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isCopySuccess() {
        return copySuccess;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CopyInformation [filePath=" + filePath + ", copySuccess=" + copySuccess + ", message=" + message + "]";
    }

}
