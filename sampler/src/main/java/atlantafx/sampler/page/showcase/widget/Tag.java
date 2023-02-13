/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import static javafx.scene.control.ContentDisplay.RIGHT;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Tag extends Button {

    public static final String CSS = """
        .tag {
          -fx-padding: 4px 6px 4px 6px;
          -fx-cursor: hand;
          -color-button-border-hover:   -color-button-border;
          -color-button-border-focused: -color-button-border;
          -color-button-border-pressed: -color-button-border;
        }
        """;

    public Tag(String text) {
        this(text, null);
    }

    public Tag(String text, Node graphic) {
        super(text, graphic);

        if (graphic != null) {
            graphic.setOnMouseEntered(e -> {
                if (getContentDisplay() == RIGHT) {
                    graphic.setScaleX(1.2);
                    graphic.setScaleY(1.2);
                }
            });
            graphic.setOnMouseExited(e -> {
                if (getContentDisplay() == RIGHT) {
                    graphic.setScaleX(1);
                    graphic.setScaleY(1);
                }
            });
            graphic.setOnMouseClicked(e -> {
                if (getContentDisplay() == RIGHT && getParent() != null && getParent() instanceof Pane pane) {
                    pane.getChildren().remove(this);
                }
            });
        }

        getStyleClass().add("tag");
    }
}
