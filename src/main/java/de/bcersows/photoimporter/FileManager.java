package de.bcersows.photoimporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import de.bcersows.photoimporter.model.CopyInformation;
import de.bcersows.photoimporter.model.FileException;
import javafx.util.Callback;

/**
 * Manager for all the file handling.
 * 
 * @author BCE
 */
public class FileManager {
    private static final String FILE_TYPE_GIF = "gif";
    private static final String FILE_TYPE_PNG = "png";
    private static final String FILE_TYPE_JPG = "jpg";
    private static final String FILE_TYPE_RAW = "arw";
    private final String[] validFileTypes;

    /** Initialize the file manager. **/
    public FileManager() {
        this.validFileTypes = new String[] { FILE_TYPE_GIF, FILE_TYPE_JPG, FILE_TYPE_PNG, FILE_TYPE_RAW };
    }

    /**
     * Search the provided paths for files and collect the information
     * 
     * @param paths
     *            the paths to search for files at
     * @return a map of file name to the actual file instance
     * @throws FileException
     *             any occurring exception
     */
    public Map<String, File> findFiles(final String... paths) throws FileException {
        final Map<String, File> files = new HashMap<>();

        for (final String path : paths) {
            if (isValidPath(path)) {
                final File source = new File(path);

                if (source.exists() && source.canRead()) {
                    final Collection<File> fileList = FileUtils.listFiles(source, validFileTypes, true);
                    files.putAll(fileList.stream().parallel().collect(Collectors.toMap(File::getName, Function.identity(), (file1, file2) -> file1)));
                }
            } else {
                throw new FileException("Source file invalid.");
            }
        }

        return files;
    }

    /**
     * Checks if is valid path.
     * 
     * @param path
     * @return true, if is valid path
     */
    private boolean isValidPath(final String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    /** Copy the provided files to the given destination folder path. **/
    public int copyFiles(final Map<String, File> filesToUpdate, final String destinationPath, final Callback<CopyInformation, Void> statusUpdateCallback)
            throws IOException {
        final int amount = 0;

        final File destDir = new File(destinationPath);

        // create the destination folder
        FileUtils.forceMkdir(destDir);

        // copy every single file, update the state accordingly
        for (final File file : filesToUpdate.values()) {
            final String msg = "Copying " + file.getAbsolutePath() + "...";
            System.out.println(msg);
            statusUpdateCallback.call(new CopyInformation(file.getAbsolutePath(), true, msg));

            // TODO bce bce maybe catch some exceptions and set the success state to false?
            FileUtils.copyFileToDirectory(file, destDir);

            // FIXME bce bce remove me
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {

            }

        }

        return amount;
    }
}
