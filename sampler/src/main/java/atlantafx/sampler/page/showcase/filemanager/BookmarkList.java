package atlantafx.sampler.page.showcase.filemanager;

import static atlantafx.sampler.page.showcase.filemanager.Model.USER_HOME;

import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

final class BookmarkList extends ListView<Bookmark> {

    public BookmarkList(Model model) {
        getStyleClass().add("bookmark-list");

        // this is Linux specific and only for EN locale
        getItems().setAll(
            new Bookmark("Home", USER_HOME, Feather.HOME),
            new Bookmark("Documents", USER_HOME.resolve("Documents"), Feather.FILE),
            new Bookmark("Downloads", USER_HOME.resolve("Downloads"), Feather.DOWNLOAD),
            new Bookmark("Music", USER_HOME.resolve("Music"), Feather.MUSIC),
            new Bookmark("Pictures", USER_HOME.resolve("Pictures"), Feather.IMAGE),
            new Bookmark("Videos", USER_HOME.resolve("Videos"), Feather.VIDEO)
        );

        setCellFactory(param -> {
            var cell = new BookmarkCell();
            cell.setOnMousePressed((MouseEvent event) -> {
                if (cell.isEmpty() || cell.getItem() == null) {
                    event.consume();
                } else {
                    Path path = cell.getItem().path();

                    if (!Files.exists(path)) {
                        var alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error Dialog");
                        alert.setHeaderText("Sorry, this is just demo.");
                        alert.setContentText("There's no such directory as \"" + path + "\"");
                        alert.initOwner(getScene().getWindow());
                        alert.showAndWait();
                        return;
                    }

                    model.navigate(path, true);
                }
            });
            return cell;
        });
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class BookmarkCell extends ListCell<Bookmark> {

        public BookmarkCell() {
            setGraphicTextGap(10);
        }

        @Override
        protected void updateItem(Bookmark bookmark, boolean empty) {
            super.updateItem(bookmark, empty);

            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new FontIcon(bookmark.icon()));
                setText(bookmark.title());
            }
        }
    }
}
