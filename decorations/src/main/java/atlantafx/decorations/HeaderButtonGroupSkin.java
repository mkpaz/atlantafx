/* SPDX-License-Identifier: MIT */

package atlantafx.decorations;

import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

/**
 * The default skin for the {@link HeaderButtonGroup}.
 */
public class HeaderButtonGroupSkin extends SkinBase<HeaderButtonGroup> {

    protected HeaderButtonGroupSkin(HeaderButtonGroup control) {
        super(control);

        var root = new HBox();
        root.setAlignment(Pos.TOP_LEFT);
        root.setFillHeight(false);
        root.getStyleClass().setAll("container");
        root.getChildren().setAll(control.getButtons());

        getChildren().setAll(root);
    }
}
