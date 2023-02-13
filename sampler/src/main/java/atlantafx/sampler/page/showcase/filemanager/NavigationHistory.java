/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

final class NavigationHistory {

    private final IntegerProperty cursor = new SimpleIntegerProperty(0);
    private final List<Path> history = new ArrayList<>();
    private final BooleanBinding canGoBack = Bindings.createBooleanBinding(
        () -> cursor.get() > 0 && history.size() > 1, cursor);
    private final BooleanBinding canGoForth = Bindings.createBooleanBinding(
        () -> cursor.get() < history.size() - 1, cursor);

    public void append(Path path) {
        if (path == null) {
            return;
        }
        var lastPath = history.size() > 0 ? history.get(history.size() - 1) : null;
        if (!Objects.equals(lastPath, path)) {
            history.add(path);
        }
        cursor.set(history.size() - 1);
    }

    public Optional<Path> back() {
        if (!canGoBack.get()) {
            return Optional.empty();
        }
        cursor.set(cursor.get() - 1);
        return Optional.of(history.get(cursor.get()));
    }

    public Optional<Path> forth() {
        if (!canGoForth.get()) {
            return Optional.empty();
        }
        cursor.set(cursor.get() + 1);
        return Optional.of(history.get(cursor.get()));
    }

    public BooleanBinding canGoBackProperty() {
        return canGoBack;
    }

    public BooleanBinding canGoForthProperty() {
        return canGoForth;
    }
}
