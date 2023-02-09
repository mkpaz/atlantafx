/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class LabelPage extends AbstractPage {

    public static final String NAME = "Label";

    @Override
    public String getName() {
        return NAME;
    }

    public LabelPage() {
        super();
        createView();
    }

    private void createView() {
        setUserContent(new VBox(
            PAGE_VGAP,
            expandingHBox(colorSample())
        ));
    }

    private SampleBlock colorSample() {
        var defaultLabel = new Label("default", createFontIcon());

        var accentLabel = new Label("accent", createFontIcon());
        accentLabel.getStyleClass().add(Styles.ACCENT);

        var successLabel = new Label("success", createFontIcon());
        successLabel.getStyleClass().add(Styles.SUCCESS);

        var warningLabel = new Label("warning", createFontIcon());
        warningLabel.getStyleClass().add(Styles.WARNING);

        var dangerLabel = new Label("danger", createFontIcon());
        dangerLabel.getStyleClass().add(Styles.DANGER);

        var mutedLabel = new Label("muted", createFontIcon());
        mutedLabel.getStyleClass().add(Styles.TEXT_MUTED);

        var subtleLabel = new Label("subtle", createFontIcon());
        subtleLabel.getStyleClass().add(Styles.TEXT_SUBTLE);

        var content = new VBox(
            BLOCK_VGAP,
            new Label("You can also use pseudo-classes to set Label color."),
            new Label("Note that icon inherits label color by default."),
            new FlowPane(
                BLOCK_HGAP, BLOCK_VGAP,
                defaultLabel, accentLabel, successLabel, warningLabel, dangerLabel,
                mutedLabel, subtleLabel
            ));

        return new SampleBlock("Colors", content);
    }

    private FontIcon createFontIcon(String... stylesClass) {
        var icon = new FontIcon(Material2AL.LABEL);
        icon.getStyleClass().addAll(stylesClass);
        return icon;
    }
}
