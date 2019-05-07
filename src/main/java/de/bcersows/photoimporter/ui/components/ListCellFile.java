package de.bcersows.photoimporter.ui.components;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bcersows.photoimporter.ToolConstants;
import de.bcersows.photoimporter.model.FileInformation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** A list cell for a file. **/
public class ListCellFile extends ListCell<FileInformation> {
    private static final Logger LOG = LoggerFactory.getLogger(ListCellFile.class);

    /** If an image preview shall be loaded. Will use a lot of memory. **/
    private static final boolean LOAD_IMAGE = false;

    /** Holds the image. **/
    private final ImageView imageView;

    /** The width for the status icons. **/
    private static final double ICON_WIDTH = 50;
    /** The size of the image previews. **/
    private static final double IMAGE_SIZE = 50;

    /** The information for this cell. **/
    private FileInformation fileInformation;

    public ListCellFile() {
        this.imageView = new ImageView();
        this.imageView.setFitHeight(ICON_WIDTH);
        this.imageView.setFitWidth(ICON_WIDTH);
    }

    /** Flag this file as copied. **/
    public void setCopied() {
        if (null != fileInformation) {
            this.fileInformation.setCopied();
        }
    }

    @Override
    protected void updateItem(@Nullable final FileInformation item, final boolean empty) {
        super.updateItem(item, empty);

        if (null != item && !empty) {
            this.fileInformation = item;
            setText(item.getPath());
            setGraphic(getCopyGraphic(item.getPath(), item.isWasCopied()));
        } else {
            setText(null);
            setGraphic(null);
            this.fileInformation = null;
        }
    }

    /** Calculate the graphic based on the copy state. **/
    private Node getCopyGraphic(final String path, final boolean wasCopied) {
        final HBox graphicContent = new HBox();
        double width = 0;

        if (LOAD_IMAGE) {// add an image preview, if configured
            final String imageFilePath = (new File(path)).getAbsolutePath();
            try (final InputStream is = new BufferedInputStream(new FileInputStream(imageFilePath));) {
                final Image image = new Image(is, IMAGE_SIZE, IMAGE_SIZE, true, false);
                this.imageView.setImage(image);
                graphicContent.getChildren().add(imageView);
                width += IMAGE_SIZE;
            } catch (IllegalArgumentException | NullPointerException | IOException e) {
                LOG.warn("Could not load image: {}", e.getMessage(), e);
            }
        }

        {// add the copy icon
            final Label graphicPane = new Label(wasCopied ? ToolConstants.ICONS.FA_TASKS.code : ToolConstants.ICONS.FA_COPY.code);
            graphicPane.setPrefWidth(ICON_WIDTH);
            graphicPane.setMinWidth(ICON_WIDTH);
            graphicPane.setMaxWidth(ICON_WIDTH);
            graphicPane.getStyleClass().add(ToolConstants.CSS_CLASS_FONT_AWESOME);
            graphicContent.getChildren().add(graphicPane);
            width += ICON_WIDTH;
        }

        graphicContent.setPrefWidth(width);
        graphicContent.setMinWidth(width);
        graphicContent.setMaxWidth(width);
        return graphicContent;
    }

    @Override
    public String toString() {
        return "ListCellFile [fileInformation=" + fileInformation + "]";
    }
}
