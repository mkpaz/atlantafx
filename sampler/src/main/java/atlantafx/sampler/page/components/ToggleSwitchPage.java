/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public final class ToggleSwitchPage extends OutlinePage {

    public static final String NAME = "ToggleSwitch";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public ToggleSwitchPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]ToggleSwitch[/i] is a control used to activate or deactivate a feature. \
            It consists of a horizontal bar with a small knob that can be moved to turn on \
            or off a function."""
        );
        addSection("Usage", usageExample());
        addSection("Color", colorExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var toggle1 = new ToggleSwitch("Enabled");
        toggle1.selectedProperty().addListener(
            (obs, old, val) -> toggle1.setText(val ? "Enabled" : "Disabled")
        );
        toggle1.setSelected(true);

        var toggle2 = new ToggleSwitch("Disabled");
        toggle2.selectedProperty().addListener(
            (obs, old, val) -> toggle2.setText(val ? "Enabled" : "Disabled")
        );
        toggle2.setLabelPosition(HorizontalDirection.RIGHT);
        toggle2.setSelected(false);
        //snippet_1:end

        var box = new HBox(HGAP_30, toggle1, toggle2);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The text position can be changed by setting the [code]labelPosition[/code] \
            property value."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox colorExample() {
        //snippet_2:start
        var successToggle = new ToggleSwitch("Enabled");
        successToggle.selectedProperty().addListener((obs, old, val) -> {
                successToggle.setText(val ? "Enabled" : "Disabled");
                successToggle.pseudoClassStateChanged(Styles.STATE_SUCCESS, val);
            }
        );
        successToggle.setSelected(true);
        successToggle.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);

        var dangerToggle = new ToggleSwitch("Disabled");
        dangerToggle.selectedProperty().addListener((obs, old, val) -> {
                dangerToggle.setText(val ? "Enabled" : "Disabled");
                dangerToggle.pseudoClassStateChanged(Styles.STATE_DANGER, val);
            }
        );
        dangerToggle.setLabelPosition(HorizontalDirection.RIGHT);
        dangerToggle.setSelected(false);
        //snippet_2:end

        var box = new HBox(HGAP_30, successToggle, dangerToggle);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the [i]ToggleSwitch[/i] color.""");

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }
}
