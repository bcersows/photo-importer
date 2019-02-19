package de.bcersows.photoimporter.ui.components;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bcersows.photoimporter.helper.FxPlatformHelper;
import de.bcersows.photoimporter.model.FileInformation;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Cell factory for the file list cells.
 * 
 * @author BCE
 */
public class ListCellFileFactory implements Callback<ListView<FileInformation>, ListCell<FileInformation>> {
    private static final Logger LOG = LoggerFactory.getLogger(ListCellFileFactory.class);

    private final ListProperty<FileInformation> items = new SimpleListProperty<FileInformation>(
            FXCollections.observableList(new LinkedList<FileInformation>()));
    private final ListView<FileInformation> listFiles;

    public ListCellFileFactory(final ListView<FileInformation> listFilesToUpdate) {
        this.listFiles = listFilesToUpdate;
        this.listFiles.setCellFactory(this);

        this.listFiles.itemsProperty().bind(this.items);
    }

    @Override
    public ListCellFile call(final ListView<FileInformation> listView) {
        return new ListCellFile();
    }

    /** Set the items of the list. **/
    public void setItems(final List<FileInformation> items) {
        LOG.info("Set {} items.", items.size());
        this.items.get().setAll(items);
    }

    /** Clear the items. **/
    public void clear() {
        FxPlatformHelper.runOnFxThread(() -> {
            this.items.clear();
            this.listFiles.getItems().clear();
        });
    }

    /** Update the copy state of the given file path. **/
    public void updateCopyState(final String filePath, final boolean copySuccess) {
        this.items.stream().filter(item -> StringUtils.equalsIgnoreCase(item.getPath(), filePath)).forEach(found -> {
            found.setWasCopied(copySuccess);
        });
        this.listFiles.refresh();
    }

}
