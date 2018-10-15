package de.bcersows.photoimporter.ui.components;

import com.sun.javafx.scene.control.skin.ListCellSkin;

import de.bcersows.photoimporter.ToolConstants;
import de.bcersows.photoimporter.model.FileInformation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/** A list cell for a file. **/
public class ListCellFile extends ListCell<FileInformation> {
    /** The width for the status icons. **/
    private static final double ICON_WIDTH = 50;
    /** The information for this cell. **/
    private final FileInformation fileInformation;

    @SuppressWarnings("unused")
    private ListCellFile() {
        // nothing
        this.fileInformation = null;
    }

    public ListCellFile(final FileInformation fileInformation) {
        this.fileInformation = fileInformation;

        // setSkin(new ListCellFileSkin(this));
    }

    /** Flag this file as copied. **/
    public void setCopied() {
        this.fileInformation.setCopied();
    }

    @Override
    protected void updateItem(final FileInformation item, final boolean empty) {
        super.updateItem(item, empty);

        if (null != item) {
            setText(item.getPath());
            setGraphic(getCopyGraphic(item.isWasCopied()));
        }
    }

    /** Calculate the graphic based on the copy state. **/
    private Node getCopyGraphic(final boolean wasCopied) {
        final Label graphicPane = new Label(wasCopied ? ToolConstants.ICONS.FA_TASKS.code : ToolConstants.ICONS.FA_COPY.code);
        graphicPane.setPrefWidth(ICON_WIDTH);
        graphicPane.setMinWidth(ICON_WIDTH);
        graphicPane.setMaxWidth(ICON_WIDTH);
        graphicPane.getStyleClass().add(ToolConstants.CSS_CLASS_FONT_AWESOME);
        return graphicPane;
    }

    @Override
    public String toString() {
        return "ListCellFile [fileInformation=" + fileInformation + "]";
    }

    private class ListCellFileSkin extends ListCellSkin<FileInformation> {
        public ListCellFileSkin(final ListCell<FileInformation> control) {
            super(control);

            System.out.println("ListView: " + control.getListView());
        }

    }
}
