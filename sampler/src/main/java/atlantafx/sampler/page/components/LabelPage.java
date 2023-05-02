/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.AbstractPage;
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

        addFormattedText("""
            Label is a non-editable text control. A [i]Label[/i] is useful for displaying text that \
            is required to fit within a specific space, and thus may need to use an ellipsis \
            or truncation to size the string to fit."""
        );
        addNode(colorExample());
    }

    private VBox colorExample() {
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

        var description = BBCodeParser.createFormattedText("""
            You can use pseudo-classes to set the [i]Label[/i] color. Note that icon \
            inherits label color by default."""
        );

        var labels = new FlowPane(
            20, 20,
            defaultLabel,
            accentLabel,
            successLabel,
            warningLabel,
            dangerLabel,
            mutedLabel,
            subtleLabel
        );

        return new VBox(20, description, labels);
    }

    private FontIcon createFontIcon(String... stylesClass) {
        var icon = new FontIcon(Material2AL.LABEL);
        icon.getStyleClass().addAll(stylesClass);
        return icon;
    }
}
