/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.util.Colour;
import atlantafx.sampler.layout.ModalDialog;

class ContrastCheckerDialog extends ModalDialog {

    private final ContrastChecker contrastChecker;

    public ContrastCheckerDialog(Colour bgBaseColor) {
        super();

        this.contrastChecker = new ContrastChecker(bgBaseColor);

        contrastChecker.getBgColor().addListener((_, _, _) -> updateStyle());
        contrastChecker.getFgColor().addListener((_, _, _) -> updateStyle());

        getStyleClass().add("contrast-checker-dialog");
        header.setTitle("Contrast Checker");
        content.setBody(contrastChecker);
        content.setFooter(null);
    }

    private void updateStyle() {
        setStyle(String.format("-color-contrast-checker-bg:%s;-color-contrast-checker-fg:%s;",
            contrastChecker.getFlatBgColor().toHex(),
            contrastChecker.getSafeFgColor().toHex()
        ));
    }

    public ContrastChecker getContent() {
        return contrastChecker;
    }
}
