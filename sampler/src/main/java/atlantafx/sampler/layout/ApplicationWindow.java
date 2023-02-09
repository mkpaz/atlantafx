/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.sampler.util.Containers;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class ApplicationWindow extends AnchorPane {

    public ApplicationWindow() {
        // this is the place to apply user custom CSS,
        // one level below the ':root'
        var body = new StackPane();
        body.getStyleClass().add("body");
        body.getChildren().setAll(
            new Overlay(),
            new MainLayer()
        );
        Containers.setAnchors(body, Insets.EMPTY);

        getChildren().setAll(body);
    }
}
