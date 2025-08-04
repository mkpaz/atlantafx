/* SPDX-License-Identifier: MIT */

package atlantafx.base.layout;

import atlantafx.base.theme.Styles;
import javafx.scene.layout.Pane;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

@NullMarked
public class InputGroupTest {

    @Test
    public void testInitSingleNode() {
        var g = new InputGroup(
            new Pane()
        );

        Assertions.assertThat(g.getChildren().size()).isEqualTo(1);
        Assertions.assertThat(g.getChildren().get(0).getStyleClass()).isEmpty();
    }

    @Test
    public void testInitTwoNodes() {
        var g = new InputGroup(
            new Pane(), new Pane()
        );

        Assertions.assertThat(g.getChildren().size()).isEqualTo(2);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.RIGHT_PILL);
    }

    @Test
    public void testInitMultipleNodes() {
        var g = new InputGroup(
            new Pane(), new Pane(), new Pane(), new Pane()
        );

        Assertions.assertThat(g.getChildren().size()).isEqualTo(4);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.CENTER_PILL);
        assertStyle(g, 2, Styles.CENTER_PILL);
        assertStyle(g, 3, Styles.RIGHT_PILL);
    }

    @Test
    public void testAddNodes() {
        var g = new InputGroup();
        Assertions.assertThat(g.getChildren()).isEmpty();

        g.getChildren().add(new Pane());
        Assertions.assertThat(g.getChildren().size()).isEqualTo(1);
        Assertions.assertThat(g.getChildren().get(0).getStyleClass()).isEmpty();

        g.getChildren().add(new Pane());
        Assertions.assertThat(g.getChildren().size()).isEqualTo(2);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.RIGHT_PILL);

        g.getChildren().add(new Pane());
        g.getChildren().add(new Pane());
        Assertions.assertThat(g.getChildren().size()).isEqualTo(4);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.CENTER_PILL);
        assertStyle(g, 2, Styles.CENTER_PILL);
        assertStyle(g, 3, Styles.RIGHT_PILL);
    }

    @Test
    public void testRemoveNodes() {
        var g = new InputGroup(
            new Pane(), new Pane(), new Pane(), new Pane()
        );
        Assertions.assertThat(g.getChildren().size()).isEqualTo(4);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.CENTER_PILL);
        assertStyle(g, 2, Styles.CENTER_PILL);
        assertStyle(g, 3, Styles.RIGHT_PILL);

        g.getChildren().remove(0);
        Assertions.assertThat(g.getChildren().size()).isEqualTo(3);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.CENTER_PILL);
        assertStyle(g, 2, Styles.RIGHT_PILL);

        g.getChildren().remove(0);
        Assertions.assertThat(g.getChildren().size()).isEqualTo(2);
        assertStyle(g, 0, Styles.LEFT_PILL);
        assertStyle(g, 1, Styles.RIGHT_PILL);

        g.getChildren().remove(0);
        Assertions.assertThat(g.getChildren().size()).isEqualTo(1);
        Assertions.assertThat(g.getChildren().get(0).getStyleClass()).isEmpty();
    }

    private void assertStyle(InputGroup g, int index, String style) {
        Assertions.assertThat(g.getChildren().get(index).getStyleClass()).containsExactly(style);
    }
}
