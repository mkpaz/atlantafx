/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import org.jetbrains.annotations.Nullable;

public abstract class TileBase extends Control {

    public TileBase() {
        this(null, null, null);
    }

    public TileBase(@Nullable @NamedArg("title") String title,
                    @Nullable @NamedArg("description") String description) {
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
     * The property representing the tile’s graphic node. It is commonly used
     * to add images or icons that are associated with the tile.
     */
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");

    public Node getGraphic() {
        return graphic.get();
    }

    public ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic.set(graphic);
    }

    /**
     * The property representing the tile’s title. Although it is not mandatory,
     * you typically would not want to have a tile without a title.
     */
    private final StringProperty title = new SimpleStringProperty(this, "title");

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * The property representing the tile’s description.
     */
    private final StringProperty description = new SimpleStringProperty(this, "description");

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
