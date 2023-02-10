/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class Stepper extends HBox {

    public static final String CSS = """
        .stepper {
          -color-stepper-bg: -color-bg-subtle;
          -color-stepper-fg: -color-fg-default;
          -color-stepper-border: -color-border-default;
          -fx-pref-width: 600px;
          -fx-spacing: 10px;
        }
        .stepper > .item {
          -fx-graphic-text-gap: 10px;
        }
        .stepper > .item > .graphic {
          -fx-font-size:  1.1em;
          -fx-min-width:  2.2em;
          -fx-max-width:  2.2em;
          -fx-min-height: 2.2em;
          -fx-max-height: 2.2em;
          -fx-text-fill: -color-stepper-fg;
          -fx-background-color: -color-stepper-bg;
          -fx-background-radius: 10em;
          -fx-border-color: -color-stepper-border;
          -fx-border-radius: 10em;
          -fx-border-width: 1;
          -fx-alignment: CENTER;
        }
        .stepper > .item .ikonli-font-icon {
          -fx-fill: -color-stepper-fg;
          -fx-icon-color: -color-stepper-fg;
        }
        .stepper > .item:selected {
          -color-stepper-bg: -color-bg-default;
          -color-stepper-fg: -color-accent-fg;
          -color-stepper-border: -color-accent-emphasis;
        }
        .stepper > .item:selected > .graphic {
          -color-stepper-bg: -color-bg-default;
          -color-stepper-fg: -color-accent-fg;
          -color-stepper-border: -color-accent-emphasis;
          -fx-border-width: 2;
        }
        .stepper > .item:completed {
          -color-stepper-bg: -color-accent-emphasis;
          -color-stepper-fg: -color-fg-emphasis;
          -color-stepper-border: -color-accent-emphasis;
        }
        """;

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass COMPLETED = PseudoClass.getPseudoClass("completed");

    private final List<Item> items;
    private final ObjectProperty<Side> textPosition = new SimpleObjectProperty<>(Side.LEFT);
    private final ObjectProperty<Item> selectedItem = new SimpleObjectProperty<>();
    private final BooleanBinding canGoBack;
    private final BooleanBinding canGoForward;

    public Stepper(Item... items) {
        this(Arrays.asList(items));
    }

    public Stepper(List<Item> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Item list can't be null or empty.");
        }

        this.items = Collections.unmodifiableList(items);

        canGoBack = Bindings.createBooleanBinding(() -> {
            if (selectedItem.get() == null) {
                return false;
            }
            var idx = items.indexOf(selectedItem.get());
            return idx > 0 && idx <= items.size() - 1;
        }, selectedItem);

        canGoForward = Bindings.createBooleanBinding(() -> {
            if (selectedItem.get() == null) {
                return false;
            }
            var idx = items.indexOf(selectedItem.get());
            return idx >= 0 && idx < items.size() - 1;
        }, selectedItem);

        selectedItem.addListener((obs, old, val) -> {
            if (old != null) {
                old.pseudoClassStateChanged(SELECTED, false);
            }
            if (val != null) {
                val.pseudoClassStateChanged(SELECTED, true);
            }
        });

        createView();
    }

    private void createView() {
        alignmentProperty().bind(Bindings.createObjectBinding(
            () -> switch (textPositionProperty().get()) {
                case TOP -> Pos.TOP_LEFT;
                case BOTTOM -> Pos.BOTTOM_LEFT;
                default -> Pos.CENTER_LEFT;
            }, textPositionProperty())
        );

        updateItems();
        getStyleClass().add("stepper");
    }

    private void updateItems() {
        var children = new ArrayList<Node>();
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            item.contentDisplayProperty().bind(Bindings.createObjectBinding(
                () -> switch (textPositionProperty().get()) {
                    case TOP -> ContentDisplay.TOP;
                    case BOTTOM -> ContentDisplay.BOTTOM;
                    case LEFT -> ContentDisplay.LEFT;
                    case RIGHT -> ContentDisplay.RIGHT;
                }, textPositionProperty())
            );

            children.add(item);

            if (i < items.size() - 1) {
                var sep = new Separator();
                HBox.setHgrow(sep, Priority.ALWAYS);
                children.add(sep);
            }
        }
        getChildren().setAll(children);
    }

    public List<Item> getItems() {
        return items;
    }

    public Side getTextPosition() {
        return textPosition.get();
    }

    public void setTextPosition(Side textPosition) {
        this.textPosition.set(textPosition);
    }

    public ObjectProperty<Side> textPositionProperty() {
        return textPosition;
    }

    public Item getSelectedItem() {
        return selectedItem.get();
    }

    public ObjectProperty<Item> selectedItemProperty() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem.set(selectedItem);
    }

    public BooleanBinding canGoBackProperty() {
        return canGoBack;
    }

    public void backward() {
        if (!canGoBack.get()) {
            return;
        }
        var idx = items.indexOf(selectedItem.get());
        selectedItem.set(items.get(idx - 1));
    }

    public BooleanBinding canGoForwardProperty() {
        return canGoForward;
    }

    public void forward() {
        if (!canGoForward.get()) {
            return;
        }
        var idx = items.indexOf(selectedItem.get());
        selectedItem.set(items.get(idx + 1));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Item extends Label {

        private final BooleanProperty completed = new SimpleBooleanProperty();

        public Item(String text) {
            super(text);

            var graphicLabel = new Label();
            graphicLabel.getStyleClass().add("graphic");
            setGraphic(graphicLabel);

            completed.addListener((obs, old, val) -> pseudoClassStateChanged(COMPLETED, val));
            getStyleClass().add("item");
            setContentDisplay(ContentDisplay.LEFT);
        }

        public void setGraphic(Ikon icon) {
            var graphicLabel = ((Label) getGraphic());
            if (icon != null) {
                graphicLabel.setText(null);
                graphicLabel.setGraphic(new FontIcon(icon));
            }
        }

        public void setGraphic(String text) {
            var graphicLabel = ((Label) getGraphic());
            if (text != null) {
                graphicLabel.setText(text);
                graphicLabel.setGraphic(null);
            }
        }

        public boolean isCompleted() {
            return completed.get();
        }

        public void setCompleted(boolean state) {
            completed.set(state);
        }

        public BooleanProperty completedProperty() {
            return completed;
        }
    }
}
