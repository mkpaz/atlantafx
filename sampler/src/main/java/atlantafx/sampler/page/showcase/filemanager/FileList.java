/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static atlantafx.sampler.page.showcase.filemanager.Utils.isFileHidden;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;

final class FileList {

    static final Comparator<Path> FILE_TYPE_COMPARATOR = Comparator.comparing(
        path -> !Files.isDirectory(path)
    );
    static final Predicate<Path> PREDICATE_ANY = path -> true;
    static final Predicate<Path> PREDICATE_NOT_HIDDEN = path -> !isFileHidden(path);

    private final ObservableList<Path> list = FXCollections.observableArrayList();
    private final ObjectProperty<Predicate<Path>> predicateProperty = new SimpleObjectProperty<>(path -> true);

    public FileList(TableView<Path> table) {
        var filteredList = new FilteredList<>(list);
        filteredList.predicateProperty().bind(predicateProperty);

        var sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(Bindings.createObjectBinding(() -> {
            Comparator<Path> tableComparator = table.comparatorProperty().get();
            return tableComparator != null
                ? FILE_TYPE_COMPARATOR.thenComparing(tableComparator)
                : FILE_TYPE_COMPARATOR;
        }, table.comparatorProperty()));
        table.setItems(sortedList);
    }

    public void load(Stream<Path> stream) {
        list.setAll(stream.collect(Collectors.toList()));
    }

    public void clear() {
        list.clear();
    }

    public ObjectProperty<Predicate<Path>> predicateProperty() {
        return predicateProperty;
    }
}
