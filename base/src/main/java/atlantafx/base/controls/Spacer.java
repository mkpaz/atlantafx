/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A spacing component used to distribute remaining width between
 * a parent's child components.
 *
 * <p>When placing a single Spacer before or after the child components,
 * the components will be pushed to the right and left of its container
 * for horizontally oriented Spacer, or to the top and bottom for vertically
 * oriented Spacer.
 *
 * <p>You can also specify the `Spacer` size. In this case, it will not be
 * extended and will work like a gap with the given size between sibling components.
 *
 * <p>Note that this control is not intended to be used in FXML unless SceneBuilder
 * supports constructor arguments, because none of the properties mentioned above are
 * observable.
 */
public class Spacer extends Region {

    /**
     * Creates a new horizontally oriented Spacer that expands
     * to fill remaining space.
     */
    public Spacer() {
        this(Orientation.HORIZONTAL);
    }

    /**
     * Creates a new Spacer with the given orientation that expands
     * to fill remaining space.
     *
     * @param orientation The orientation of the spacer.
     */
    public Spacer(Orientation orientation) {
        super();

        switch (orientation) {
            case HORIZONTAL -> HBox.setHgrow(this, Priority.ALWAYS);
            case VERTICAL -> VBox.setVgrow(this, Priority.ALWAYS);
        }
    }

    /**
     * Creates a new Spacer with the fixed size.
     *
     * @param size The size of the spacer.
     */
    public Spacer(double size) {
        this(size, Orientation.HORIZONTAL);
    }

    /**
     * Creates a new Spacer with the fixed size and orientation.
     *
     * @param size        The size of the spacer.
     * @param orientation The orientation of the spacer.
     */
    public Spacer(double size, Orientation orientation) {
        super();

        switch (orientation) {
            case HORIZONTAL -> {
                setMinWidth(size);
                setPrefWidth(size);
                setMaxWidth(size);
            }
            case VERTICAL -> {
                setMinHeight(size);
                setPrefHeight(size);
                setMaxHeight(size);
            }
        }
    }
}
