/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.filemanager;

import atlantafx.sampler.util.Containers;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static atlantafx.sampler.page.showcase.filemanager.Utils.*;
import static javafx.scene.control.TableColumn.SortType.ASCENDING;

final class TableDirectoryView extends AnchorPane implements DirectoryView {

    private static final PseudoClass HIDDEN = PseudoClass.getPseudoClass("hidden");
    private static final PseudoClass FOLDER = PseudoClass.getPseudoClass("folder");

    private final FileList fileList;
    private Consumer<Path> actionHandler;

    public TableDirectoryView() {
        TableView<Path> table = createTable();
        fileList = new FileList(table);

        getChildren().setAll(table);
        getStyleClass().add("table-directory-view");
        Containers.setAnchors(table, Insets.EMPTY);
    }

    @SuppressWarnings("unchecked")
    private TableView<Path> createTable() {
        var filenameCol = new TableColumn<Path, String>("Name");
        filenameCol.setCellValueFactory(param -> new SimpleStringProperty(
                param.getValue() != null ? param.getValue().getFileName().toString() : null
        ));
        filenameCol.setComparator(Comparator.comparing(String::toLowerCase));
        filenameCol.setSortType(ASCENDING);
        filenameCol.setCellFactory(col -> new FilenameCell());

        var sizeCol = new TableColumn<Path, Number>("Size");
        sizeCol.setCellValueFactory(param -> new SimpleLongProperty(fileSize(param.getValue())));
        sizeCol.setCellFactory(col -> new FileSizeCell());

        var mtimeCol = new TableColumn<Path, FileTime>("Modified");
        mtimeCol.setCellValueFactory(param -> new SimpleObjectProperty<>(fileMTime(param.getValue())));
        mtimeCol.setCellFactory(col -> new FileTimeCell());

        // ~

        var table = new TableView<Path>();
        table.getColumns().setAll(filenameCol, sizeCol, mtimeCol);
        table.getSortOrder().add(filenameCol);
        table.setSortPolicy(param -> true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        filenameCol.minWidthProperty().bind(table.widthProperty().multiply(0.5));
        table.setRowFactory(param -> {
            TableRow<Path> row = new TableRow<>();

            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty() && actionHandler != null) {
                    actionHandler.accept(row.getItem());
                }
            });

            return row;
        });

        return table;
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public void setDirectory(Path path) {
        if (path == null) {
            fileList.clear();
        } else {
            try (Stream<Path> stream = Files.list(path)) {
                fileList.load(stream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void setOnAction(Consumer<Path> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public FileList getFileList() {
        return fileList;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class FilenameCell extends TableCell<Path, String> {

        private final FontIcon icon = new FontIcon();

        public FilenameCell() {
            setGraphicTextGap(10);
        }

        @Override
        protected void updateItem(String filename, boolean empty) {
            super.updateItem(filename, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setGraphic(null);
                setText(null);
            } else {
                boolean isDirectory = Files.isDirectory(getTableRow().getItem());
                icon.setIconCode(isDirectory ? Material2AL.FOLDER : Material2OutlinedAL.INSERT_DRIVE_FILE);
                pseudoClassStateChanged(FOLDER, isDirectory);
                getTableRow().pseudoClassStateChanged(HIDDEN, isFileHidden(getTableRow().getItem()));
                setGraphic(icon);
                setText(filename);
            }
        }
    }

    private static class FileSizeCell extends TableCell<Path, Number> {

        @Override
        protected void updateItem(Number fileSize, boolean empty) {
            super.updateItem(fileSize, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setText(null);
            } else {
                Path path = getTableRow().getItem();
                if (Files.isDirectory(path)) {
                    if (Files.isReadable(path)) {
                        try (Stream<Path> stream = Files.list(path)) {
                            setText(stream.count() + " items");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        setText("unknown");
                    }
                } else {
                    setText(humanReadableByteCount(fileSize.longValue()));
                }
            }
        }
    }

    private static class FileTimeCell extends TableCell<Path, FileTime> {

        @Override
        protected void updateItem(FileTime fileTime, boolean empty) {
            super.updateItem(fileTime, empty);
            if (empty) {
                setText(null);
            } else {
                setText(DateTimeFormatter.ISO_DATE.format(
                        fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                ));
            }
        }
    }
}
