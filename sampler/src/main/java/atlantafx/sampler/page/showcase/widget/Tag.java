/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.widget;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class Tag extends Button {

    public static final String CSS = """
            .tag {
              -fx-padding: 4px 6px 4px 6px;
              -fx-cursor: hand;
              -color-button-border-focused: -color-button-border;
              -color-button-border-pressed: -color-button-border;
            }
            """;

    public Tag(String text) {
        this(text, null);
    }

    public Tag(String text, Node graphic) {
        super(text, graphic);
        getStyleClass().add("tag");
    }
}
