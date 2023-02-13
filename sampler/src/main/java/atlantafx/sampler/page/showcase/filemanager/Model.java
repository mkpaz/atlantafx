/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

final class Model {

    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    private final ReadOnlyObjectWrapper<Path> currentPath = new ReadOnlyObjectWrapper<>();
    private final NavigationHistory history = new NavigationHistory();

    public Model() {
        currentPath.set(USER_HOME);
        history.append(USER_HOME);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    public ReadOnlyObjectProperty<Path> currentPathProperty() {
        return currentPath.getReadOnlyProperty();
    }

    public NavigationHistory getHistory() {
        return history;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void back() {
        history.back().ifPresent(currentPath::set);
    }

    public void forth() {
        history.forth().ifPresent(currentPath::set);
    }

    public void navigate(Path path, boolean saveInHistory) {
        currentPath.set(Objects.requireNonNullElse(path, USER_HOME));
        if (saveInHistory) {
            history.append(path);
        }
    }
}
