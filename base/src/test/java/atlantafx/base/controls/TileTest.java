/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class TileTest {

    private Tile tile;

    @BeforeEach
    public void setUp() {
        tile = new Tile();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(tile.getStyleClass()).contains("tile");
        assertThat(tile.getStyleClass()).contains("tile-base");
    }

    @Test
    public void testDefaultPropertyValues() {
        assertThat(tile.getTitle()).isNull();
        assertThat(tile.getDescription()).isNull();
        assertThat(tile.getGraphic()).isNull();
        assertThat(tile.getAction()).isNull();
        assertThat(tile.getActionHandler()).isNull();
    }

    @Test
    public void testConstructorNoArgs() {
        var t = new Tile();
        assertThat(t.getTitle()).isNull();
        assertThat(t.getDescription()).isNull();
        assertThat(t.getGraphic()).isNull();
    }

    @Test
    public void testConstructorTitleDescription() {
        var t = new Tile("Title", "Description");
        assertThat(t.getTitle()).isEqualTo("Title");
        assertThat(t.getDescription()).isEqualTo("Description");
        assertThat(t.getGraphic()).isNull();
    }

    @Test
    public void testConstructorTitleDescriptionGraphic() {
        var graphic = new Label("Icon");
        var t = new Tile("Title", "Description", graphic);
        assertThat(t.getTitle()).isEqualTo("Title");
        assertThat(t.getDescription()).isEqualTo("Description");
        assertThat(t.getGraphic()).isSameAs(graphic);
    }

    @Test
    public void testTitleProperty() {
        tile.setTitle("My Title");
        assertThat(tile.getTitle()).isEqualTo("My Title");
        assertThat(tile.titleProperty().get()).isEqualTo("My Title");

        tile.setTitle(null);
        assertThat(tile.getTitle()).isNull();
    }

    @Test
    public void testDescriptionProperty() {
        tile.setDescription("My Description");
        assertThat(tile.getDescription()).isEqualTo("My Description");
        assertThat(tile.descriptionProperty().get()).isEqualTo("My Description");

        tile.setDescription(null);
        assertThat(tile.getDescription()).isNull();
    }

    @Test
    public void testGraphicProperty() {
        var graphic = new Label("Graphic");
        tile.setGraphic(graphic);
        assertThat(tile.getGraphic()).isSameAs(graphic);
        assertThat(tile.graphicProperty().get()).isSameAs(graphic);

        tile.setGraphic(null);
        assertThat(tile.getGraphic()).isNull();
    }

    @Test
    public void testActionProperty() {
        var action = new Button("Click");
        tile.setAction(action);
        assertThat(tile.getAction()).isSameAs(action);
        assertThat(tile.actionProperty().get()).isSameAs(action);

        tile.setAction(null);
        assertThat(tile.getAction()).isNull();
    }

    @Test
    public void testActionHandlerProperty() {
        Runnable handler = () -> {};
        tile.setActionHandler(handler);
        assertThat(tile.getActionHandler()).isSameAs(handler);
        assertThat(tile.actionHandlerProperty().get()).isSameAs(handler);

        tile.setActionHandler(null);
        assertThat(tile.getActionHandler()).isNull();
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = tile.createDefaultSkin();
        assertThat(skin).isInstanceOf(TileSkin.class);
    }
}
