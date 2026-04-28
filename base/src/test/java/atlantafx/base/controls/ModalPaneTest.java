/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class ModalPaneTest {

    private ModalPane modalPane;

    @BeforeEach
    public void setUp() {
        modalPane = new ModalPane();
    }

    @Test
    public void testDefaultStyleClass() {
        // Style class is added by the skin, so we trigger skin creation
        modalPane.createDefaultSkin();
        assertThat(modalPane.getStyleClass()).contains("modal-pane");
    }

    @Test
    public void testDefaultConstructorTopViewOrder() {
        assertThat(modalPane.getTopViewOrder()).isEqualTo(ModalPane.Z_FRONT);
    }

    @Test
    public void testCustomTopViewOrderConstructor() {
        var pane = new ModalPane(-5);
        assertThat(pane.getTopViewOrder()).isEqualTo(-5);
    }

    @Test
    public void testContentPropertyDefaultsToNull() {
        assertThat(modalPane.getContent()).isNull();
    }

    @Test
    public void testContentPropertySetterGetter() {
        var node = new VBox();
        modalPane.setContent(node);
        assertThat(modalPane.getContent()).isSameAs(node);

        modalPane.setContent(null);
        assertThat(modalPane.getContent()).isNull();
    }

    @Test
    public void testDisplayPropertyDefaultsToFalse() {
        assertThat(modalPane.isDisplay()).isFalse();
    }

    @Test
    public void testDisplayPropertySetterGetter() {
        modalPane.setDisplay(true);
        assertThat(modalPane.isDisplay()).isTrue();

        modalPane.setDisplay(false);
        assertThat(modalPane.isDisplay()).isFalse();
    }

    @Test
    public void testAlignmentPropertyDefaultsToCenter() {
        assertThat(modalPane.getAlignment()).isEqualTo(Pos.CENTER);
    }

    @Test
    public void testAlignmentPropertySetterGetter() {
        modalPane.setAlignment(Pos.TOP_LEFT);
        assertThat(modalPane.getAlignment()).isEqualTo(Pos.TOP_LEFT);

        modalPane.setAlignment(Pos.BOTTOM_RIGHT);
        assertThat(modalPane.getAlignment()).isEqualTo(Pos.BOTTOM_RIGHT);
    }

    @Test
    public void testPersistentPropertyDefaultsToFalse() {
        assertThat(modalPane.getPersistent()).isFalse();
    }

    @Test
    public void testPersistentPropertySetterGetter() {
        modalPane.setPersistent(true);
        assertThat(modalPane.getPersistent()).isTrue();

        modalPane.setPersistent(false);
        assertThat(modalPane.getPersistent()).isFalse();
    }

    @Test
    public void testTransitionFactoriesDefaultToNonNull() {
        assertThat(modalPane.getInTransitionFactory()).isNotNull();
        assertThat(modalPane.getOutTransitionFactory()).isNotNull();
    }

    @Test
    public void testTransitionFactoriesCanBeSetToNull() {
        modalPane.setInTransitionFactory(null);
        assertThat(modalPane.getInTransitionFactory()).isNull();

        modalPane.setOutTransitionFactory(null);
        assertThat(modalPane.getOutTransitionFactory()).isNull();
    }

    @Test
    public void testShowSetsContentAndDisplay() {
        var node = new VBox();
        modalPane.show(node);

        assertThat(modalPane.getContent()).isSameAs(node);
        assertThat(modalPane.isDisplay()).isTrue();
    }

    @Test
    public void testHideWithoutClearKeepsContent() {
        var node = new VBox();
        modalPane.show(node);
        modalPane.hide(false);

        assertThat(modalPane.isDisplay()).isFalse();
        assertThat(modalPane.getContent()).isSameAs(node);
    }

    @Test
    public void testHideWithClearRemovesContent() {
        var node = new VBox();
        modalPane.show(node);
        modalPane.hide(true);

        assertThat(modalPane.isDisplay()).isFalse();
        assertThat(modalPane.getContent()).isNull();
    }

    @Test
    public void testHideDefaultDoesNotClearContent() {
        var node = new VBox();
        modalPane.show(node);
        modalPane.hide();

        assertThat(modalPane.isDisplay()).isFalse();
        assertThat(modalPane.getContent()).isSameAs(node);
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = modalPane.createDefaultSkin();
        assertThat(skin).isInstanceOf(ModalPaneSkin.class);
    }

    @Test
    public void testUsePredefinedTransitionFactoriesWithNullSide() {
        modalPane.usePredefinedTransitionFactories(null);
        assertThat(modalPane.getInTransitionFactory()).isNotNull();
        assertThat(modalPane.getOutTransitionFactory()).isNotNull();
    }

    @Test
    public void testUsePredefinedTransitionFactoriesWithSide() {
        for (var side : javafx.geometry.Side.values()) {
            modalPane.usePredefinedTransitionFactories(side);
            assertThat(modalPane.getInTransitionFactory()).isNotNull();
            assertThat(modalPane.getOutTransitionFactory()).isNotNull();
        }
    }

    @Test
    public void testZFrontAndZBackConstants() {
        assertThat(ModalPane.Z_FRONT).isEqualTo(-10);
        assertThat(ModalPane.Z_BACK).isEqualTo(10);
    }
}
