/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.util.BBCodeParser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import org.jspecify.annotations.Nullable;

/**
 * A common class for implementing tile-based controls, specifically the
 * {@link Message} and the {@link Tile}.
 */
public abstract class TileBase extends Control {

    public TileBase() {
        this(null, null, null);
    }

    public TileBase(@Nullable String title,
                    @Nullable String description) {
        this(title, description, null);
    }

    public TileBase(@Nullable String title,
                    @Nullable String description,
                    @Nullable Node graphic) {
        super();

        setTitle(title);
        setDescription(description);
        setGraphic(graphic);
        getStyleClass().add("tile-base");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the tile’s graphic node. It is commonly used to add images or icons
     * that are associated with the tile.
     */
    public ObjectProperty<@Nullable Node> graphicProperty() {
        return graphic;
    }

    private final ObjectProperty<@Nullable Node> graphic = new SimpleObjectProperty<>(this, "graphic");

    public @Nullable Node getGraphic() {
        return graphic.get();
    }

    public void setGraphic(@Nullable Node graphic) {
        this.graphic.set(graphic);
    }

    /**
     * Represents the tile’s title (or header).
     */
    public StringProperty titleProperty() {
        return title;
    }

    private final StringProperty title = new SimpleStringProperty(this, "title");

    public @Nullable String getTitle() {
        return title.get();
    }

    public void setTitle(@Nullable String title) {
        this.title.set(title);
    }

    /**
     * Represents the tile’s description (or optional text).
     *
     * <p>This property supports BBCode formatted text. Refer to the {@link BBCodeParser}
     * for more information.
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    private final StringProperty description = new SimpleStringProperty(this, "description");

    public @Nullable String getDescription() {
        return description.get();
    }

    public void setDescription(@Nullable String description) {
        this.description.set(description);
    }
}
