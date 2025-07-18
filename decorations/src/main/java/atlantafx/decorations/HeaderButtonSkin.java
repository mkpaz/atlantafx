/* SPDX-License-Identifier: MIT */

package atlantafx.decorations;

import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;

/**
 * The default skin for the {@link HeaderButton}.
 */
public class HeaderButtonSkin extends SkinBase<HeaderButton> {

    public HeaderButtonSkin(HeaderButton control) {
        super(control);

        var region = new Region();
        region.getStyleClass().setAll("region");

        switch (control.getType()) {
            case ICONIFY -> control.getStyleClass().add("minimize");
            case MAXIMIZE -> control.getStyleClass().add("maximize");
            case CLOSE -> control.getStyleClass().add("close");
        }

        getChildren().setAll(region);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }
}
