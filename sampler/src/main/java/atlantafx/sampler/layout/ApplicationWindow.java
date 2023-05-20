/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.sampler.util.NodeUtils;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public final class ApplicationWindow extends AnchorPane {

    public static final int MIN_WIDTH = 1200;
    public static final int SIDEBAR_WIDTH = 250;

    public ApplicationWindow() {
        // this is the place to apply user custom CSS,
        // one level below the ':root'
        var body = new StackPane();
        body.getStyleClass().add("body");
        body.getChildren().setAll(
            new Overlay(),
            new MainLayer()
        );
        NodeUtils.setAnchors(body, Insets.EMPTY);

        getChildren().setAll(body);
    }
}
