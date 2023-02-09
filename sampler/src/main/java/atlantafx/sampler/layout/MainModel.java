/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import static atlantafx.sampler.layout.MainModel.SubLayer.PAGE;
import static atlantafx.sampler.layout.MainModel.SubLayer.SOURCE_CODE;

import atlantafx.sampler.page.Page;
import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainModel {

    public enum SubLayer {
        PAGE,
        SOURCE_CODE
    }

    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();
    private final StringProperty searchText = new SimpleStringProperty();
    private final ReadOnlyObjectWrapper<Class<? extends Page>> selectedPage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyBooleanWrapper themeChangeToggle = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper sourceCodeToggle = new ReadOnlyBooleanWrapper();
    private final ReadOnlyObjectWrapper<SubLayer> currentSubLayer = new ReadOnlyObjectWrapper<>(PAGE);

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty themeChangeToggleProperty() {
        return themeChangeToggle.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty sourceCodeToggleProperty() {
        return sourceCodeToggle.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Class<? extends Page>> selectedPageProperty() {
        return selectedPage.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<SubLayer> currentSubLayerProperty() {
        return currentSubLayer.getReadOnlyProperty();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void setPageData(String text, boolean canChangeTheme, boolean canDisplaySource) {
        title.set(Objects.requireNonNull(text));
        themeChangeToggle.set(canChangeTheme);
        sourceCodeToggle.set(canDisplaySource);
    }

    public void navigate(Class<? extends Page> page) {
        selectedPage.set(Objects.requireNonNull(page));
        currentSubLayer.set(PAGE);
    }

    public void nextSubLayer() {
        var old = currentSubLayer.get();
        currentSubLayer.set(old == PAGE ? SOURCE_CODE : PAGE);
    }
}
