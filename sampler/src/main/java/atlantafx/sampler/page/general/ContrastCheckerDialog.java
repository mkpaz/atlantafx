/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.sampler.page.OverlayDialog;
import atlantafx.sampler.util.JColorUtils;
import atlantafx.sampler.util.NodeUtils;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.paint.Color;

class ContrastCheckerDialog extends OverlayDialog<ContrastChecker> {

    private final ContrastChecker contrastChecker;

    public ContrastCheckerDialog(ReadOnlyObjectProperty<Color> bgBaseColor) {
        this.contrastChecker = new ContrastChecker(bgBaseColor);

        contrastChecker.bgColorProperty().addListener((obs, old, val) -> updateStyle());
        contrastChecker.fgColorProperty().addListener((obs, old, val) -> updateStyle());

        getStyleClass().add("contrast-checker-dialog");
        setTitle("Contrast Checker");
        setContent(contrastChecker);
        NodeUtils.toggleVisibility(footerBox, false);
    }

    private void updateStyle() {
        setStyle(String.format("-color-dialog-bg:%s;-color-dialog-fg:%s;",
            JColorUtils.toHexWithAlpha(contrastChecker.getBgColor()),
            JColorUtils.toHexWithAlpha(contrastChecker.getSafeFgColor())
        ));
    }

    public ContrastChecker getContent() {
        return contrastChecker;
    }
}
