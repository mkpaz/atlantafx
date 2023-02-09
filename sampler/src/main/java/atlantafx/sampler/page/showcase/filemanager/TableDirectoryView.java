/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static atlantafx.sampler.page.showcase.filemanager.Utils.fileMTime;
import static atlantafx.sampler.page.showcase.filemanager.Utils.fileSize;
import static atlantafx.sampler.page.showcase.filemanager.Utils.isFileHidden;
import static atlantafx.sampler.util.HumanReadableFormat.byteCount;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static javafx.scene.control.TableColumn.SortType.ASCENDING;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.util.Containers;
import atlantafx.sampler.util.HumanReadableFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

final class TableDirectoryView extends AnchorPane implements DirectoryView {

    private static final PseudoClass HIDDEN = PseudoClass.getPseudoClass("hidden");
    private static final PseudoClass FOLDER = PseudoClass.getPseudoClass("folder");
    private static final FileIconRepository REPO = new FileIconRepository();
    private static final String UNKNOWN = "unknown";

    private final FileList fileList;
    private Consumer<Path> actionHandler;

    public TableDirectoryView() {
        TableView<Path> table = createTable();
        fileList = new FileList(table);

        getChildren().setAll(table);
        getStyleClass().addAll("table-directory-view");
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
        mtimeCol.setCellValueFactory(param -> new SimpleObjectProperty<>(fileMTime(param.getValue(), NOFOLLOW_LINKS)));
        mtimeCol.setCellFactory(col -> new FileTimeCell());
        mtimeCol.getStyleClass().add(Tweaks.ALIGN_RIGHT);

        // ~

        var table = new TableView<Path>();
        table.getStyleClass().add(Styles.STRIPED);
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
        table.setContextMenu(new RightClickMenu());

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

        private final ImageView imageView = new ImageView();

        public FilenameCell() {
            setGraphicTextGap(10);
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
        }

        @Override
        protected void updateItem(String filename, boolean empty) {
            super.updateItem(filename, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setGraphic(null);
                setText(null);
            } else {
                var isDirectory = Files.isDirectory(getTableRow().getItem());
                var path = getTableRow().getItem();

                if (!isDirectory) {
                    imageView.setImage(REPO.getByMimeType(Utils.getMimeType(path)));
                } else {
                    imageView.setImage(FileIconRepository.FOLDER);
                }

                pseudoClassStateChanged(FOLDER, isDirectory);
                getTableRow().pseudoClassStateChanged(HIDDEN, isFileHidden(path));

                setGraphic(imageView);
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
                        setText(UNKNOWN);
                    }
                } else {
                    setText(byteCount(fileSize.longValue()));
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
                setText(fileTime != null
                    ? HumanReadableFormat.date(fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    : UNKNOWN
                );
            }
        }
    }
}
