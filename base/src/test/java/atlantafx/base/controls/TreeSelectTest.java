/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.JavaFXTest;
import javafx.scene.control.TreeItem;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class TreeSelectTest {

    @Test
    public void testDefaultStyleClass() {
        var select = new TreeSelect<String>();
        assertThat(select.getStyleClass()).contains("tree-select");
    }

    @Test
    public void testPromptTextProperty() {
        var select = new TreeSelect<String>();
        assertThat(select.getPromptText()).isEmpty();

        select.setPromptText("Choose...");
        assertThat(select.getPromptText()).isEqualTo("Choose...");
    }

    @Test
    public void testRootProperty() {
        var root = new TreeItem<String>("Root");
        var select = new TreeSelect<>(root);
        assertThat(select.getRoot()).isSameAs(root);

        var newRoot = new TreeItem<String>("New");
        select.setRoot(newRoot);
        assertThat(select.getRoot()).isSameAs(newRoot);
    }

    @Test
    public void testShowRootProperty() {
        var select = new TreeSelect<String>();
        assertThat(select.isShowRoot()).isTrue();

        select.setShowRoot(false);
        assertThat(select.isShowRoot()).isFalse();
    }

    @Test
    public void testConstructorWithRoot() {
        var root = new TreeItem<String>("Root");
        var select = new TreeSelect<>(root);
        assertThat(select.getRoot()).isSameAs(root);
    }
}
