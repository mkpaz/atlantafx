/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.theme.SamplerTheme;
import java.io.IOException;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public final class ThemeThumbnail extends VBox implements Toggle {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private final RadioButton toggle; // internal helper node to manage selected state

    public ThemeThumbnail(SamplerTheme theme) {
        super();

        toggle = new RadioButton();

        try {
            Map<String, String> colors = theme.parseColors();

            var circles = new HBox(
                createCircle(colors.get("-color-fg-default"), colors.get("-color-fg-default"), false),
                createCircle(colors.get("-color-fg-default"), colors.get("-color-accent-emphasis"), true),
                createCircle(colors.get("-color-fg-default"), colors.get("-color-success-emphasis"), true),
                createCircle(colors.get("-color-fg-default"), colors.get("-color-danger-emphasis"), true),
                createCircle(colors.get("-color-fg-default"), colors.get("-color-warning-emphasis"), true)
            );
            circles.setAlignment(Pos.CENTER);

            var nameLbl = new Label(theme.getName());
            nameLbl.getStyleClass().add(Styles.TEXT_CAPTION);
            Styles.appendStyle(nameLbl, "-fx-text-fill", colors.get("-color-fg-muted"));

            setStyle("""
                -fx-background-radius: 10px, 8px;
                -fx-background-insets: 0, 3px
                """
            );
            Styles.appendStyle(
                this,
                "-fx-background-color",
                "-color-thumbnail-border," + colors.get("-color-bg-default")
            );
            setOnMouseClicked(e -> setSelected(true));
            getStyleClass().add("theme-thumbnail");
            getChildren().setAll(nameLbl, circles);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        selectedProperty().addListener(
            (obs, old, val) -> pseudoClassStateChanged(SELECTED, val)
        );
    }

    private Circle createCircle(String borderColor, String bgColor, boolean overlap) {
        var circle = new Circle(10);
        Styles.appendStyle(circle, "-fx-stroke", borderColor);
        Styles.appendStyle(circle, "-fx-fill", bgColor);
        if (overlap) {
            HBox.setMargin(circle, new Insets(0, 0, 0, -5));
        }
        return circle;
    }

    @Override
    public ToggleGroup getToggleGroup() {
        return toggle.getToggleGroup();
    }

    @Override
    public void setToggleGroup(ToggleGroup toggleGroup) {
        toggle.setToggleGroup(toggleGroup);
    }

    @Override
    public ObjectProperty<ToggleGroup> toggleGroupProperty() {
        return toggle.toggleGroupProperty();
    }

    @Override
    public boolean isSelected() {
        return toggle.isSelected();
    }

    @Override
    public void setSelected(boolean selected) {
        toggle.setSelected(selected);
    }

    @Override
    public BooleanProperty selectedProperty() {
        return toggle.selectedProperty();
    }

    @Override
    public void setUserData(Object value) {
        toggle.setUserData(value);
    }
}