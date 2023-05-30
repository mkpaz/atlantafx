/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.base.theme.Styles.TEXT_SMALL;

import atlantafx.base.theme.Tweaks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

final class IconBrowser extends TableView<List<Ikon>> {

    static final int FILTER_LEN = 2;

    private final int colNum;
    private final List<Ikon> icons;
    private final SimpleStringProperty filter = new SimpleStringProperty();

    public IconBrowser(int colNum, List<Ikon> icons) {
        super();

        if (colNum <= 0) {
            throw new IllegalArgumentException("Column count must be greater than 0.");
        }

        if (icons == null || icons.isEmpty()) {
            throw new IllegalArgumentException("Icon list cannot be null or empty.");
        }

        this.colNum = colNum;
        this.icons = icons;

        filterProperty().addListener((obs, old, val) -> updateData(val));

        initTable();
        updateData(null);
    }

    public SimpleStringProperty filterProperty() {
        return filter;
    }

    private void initTable() {
        for (int i = 0; i < colNum; i++) {
            var col = new TableColumn<List<Ikon>, Ikon>("col" + i);
            final int colIndex = i;
            col.setCellValueFactory(cb -> {
                var row = cb.getValue();
                var item = row.size() > colIndex ? row.get(colIndex) : null;
                return new SimpleObjectProperty<>(item);
            });
            col.setCellFactory(cb -> new FontIconCell());
            col.getStyleClass().add(Tweaks.ALIGN_CENTER);
            getColumns().add(col);
        }

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        getSelectionModel().setCellSelectionEnabled(true);
        getStyleClass().add("icon-browser");
    }

    private void updateData(String filterString) {
        var displayedIcons = filterString == null || filterString.isBlank() || filterString.length() < FILTER_LEN
            ? icons
            : icons.stream().filter(icon -> containsString(icon.getDescription(), filterString)).toList();

        var data = partitionList(displayedIcons, colNum);
        getItems().setAll(data);
    }

    private <T> Collection<List<T>> partitionList(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        if (list.size() == 0) {
            return partitions;
        }

        int length = list.size();
        int numOfPartitions = length / size + ((length % size == 0) ? 0 : 1);

        for (int i = 0; i < numOfPartitions; i++) {
            int from = i * size;
            int to = Math.min((i * size + size), length);
            partitions.add(list.subList(from, to));
        }
        return partitions;
    }

    private boolean containsString(String s1, String s2) {
        return s1.toLowerCase(Locale.ROOT).contains(s2.toLowerCase(Locale.ROOT));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class FontIconCell extends TableCell<List<Ikon>, Ikon> {

        private final Label root = new Label();
        private final FontIcon fontIcon = new FontIcon();

        public FontIconCell() {
            super();

            root.setContentDisplay(ContentDisplay.TOP);
            root.setGraphic(fontIcon);
            root.setGraphicTextGap(10);
            root.getStyleClass().addAll("icon-label", TEXT_SMALL);
        }

        @Override
        protected void updateItem(Ikon icon, boolean empty) {
            super.updateItem(icon, empty);

            if (icon == null) {
                setGraphic(null);
                return;
            }

            root.setText(icon.getDescription());
            fontIcon.setIconCode(icon);
            setGraphic(root);
        }
    }
}
