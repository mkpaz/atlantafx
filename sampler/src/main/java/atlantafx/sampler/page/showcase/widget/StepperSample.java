/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.page.showcase.widget.Stepper.Item;
import atlantafx.sampler.theme.CSSFragment;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

public class StepperSample extends SampleBlock {

    public StepperSample() {
        super("Stepper", createContent());
    }

    private static Node createContent() {
        var content = new VBox(BLOCK_VGAP);
        new CSSFragment(Stepper.CSS).addTo(content);

        // == STEPPER CONTENT ==

        var stackContent = new Label();
        stackContent.getStyleClass().add(Styles.TITLE_1);
        stackContent.setStyle("-fx-background-color:-color-bg-default;");
        stackContent.setWrapText(true);
        stackContent.setMinHeight(200);
        stackContent.setMaxWidth(400);
        stackContent.setAlignment(Pos.CENTER);

        var stack = new StackPane(stackContent);
        stack.setPrefHeight(200);

        // == STEPPER ==

        var firstItem = new Item("First");
        firstItem.setGraphic("A");

        var secondItem = new Item("Second");
        secondItem.setGraphic("B");

        var thirdItem = new Item("Third");
        thirdItem.setGraphic("C");

        var stepper = new Stepper(firstItem, secondItem, thirdItem);
        stepper.selectedItemProperty().addListener(
            (obs, old, val) -> stackContent.setText(val != null ? val.getText() : null)
        );
        stepper.setSelectedItem(stepper.getItems().get(0));

        // == CONTROLS ==

        var nextBtn = new Button("Next");
        nextBtn.setDefaultButton(true);
        nextBtn.setOnAction(e -> {
            // you can validate user input before moving forward here
            stepper.getSelectedItem().setCompleted(true);
            stepper.forward();
        });
        nextBtn.textProperty().bind(Bindings.createStringBinding(
            () -> stepper.canGoForwardProperty().get() ? "Next" : "Done", stepper.canGoForwardProperty())
        );

        var prevBtn = new Button("Previous");
        prevBtn.getStyleClass().addAll(Styles.FLAT);
        prevBtn.setOnAction(e -> {
            stepper.getSelectedItem().setCompleted(false);
            stepper.backward();
        });
        prevBtn.disableProperty().bind(stepper.canGoBackProperty().not());

        var iconToggle = new ToggleSwitch("Icons");
        iconToggle.selectedProperty().addListener((obs, old, val) -> {
            for (int i = 0; i < stepper.getItems().size(); i++) {
                var item = stepper.getItems().get(i);
                if (val) {
                    item.setGraphic(randomIcon());
                } else {
                    item.setGraphic(String.valueOf(i + 1));
                }
            }
        });

        var rotateBtn = new Button("Rotate", new FontIcon(Material2MZ.ROTATE_RIGHT));
        rotateBtn.setOnAction(e -> {
            Side nextSide = switch (stepper.getTextPosition()) {
                case LEFT -> Side.TOP;
                case TOP -> Side.RIGHT;
                case RIGHT -> Side.BOTTOM;
                case BOTTOM -> Side.LEFT;
            };
            stepper.setTextPosition(nextSide);
        });

        var controls = new HBox(
            BLOCK_HGAP,
            nextBtn,
            prevBtn,
            new Spacer(),
            iconToggle,
            rotateBtn
        );
        controls.setAlignment(Pos.CENTER_LEFT);

        // ~

        content.getChildren().setAll(stepper, stack, new Separator(), controls);
        return content;
    }
}
