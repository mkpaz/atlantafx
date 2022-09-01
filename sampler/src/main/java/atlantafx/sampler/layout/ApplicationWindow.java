/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.layout;

import javafx.scene.layout.StackPane;

public class ApplicationWindow extends StackPane {

    public ApplicationWindow() {
        getChildren().setAll(
                new Overlay(),
                new MainLayer()
        );
    }
}
