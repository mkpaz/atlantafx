/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.SegmentedControl;
import atlantafx.base.controls.ToggleLabel;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public final class SegmentedControlPage extends OutlinePage {

    public static final String NAME = "SegmentedControl";

    @Override
    public String getName() {
        return NAME;
    }

    public SegmentedControlPage() {
        super();

        addPageHeader();
        addFormattedText("""
            [i]SegmentedControl[/i] is a tab control flavor that allows to \
            switch between different views or options."""
        );
        addSection("Usage", usageExample());
        addSection("Icons", iconsExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var seg = new SegmentedControl("Left", "Center", "Right");

        var label = new Label();
        var content = new BorderPane(label);
        seg.getToggleGroup().selectedToggleProperty().subscribe(toggle -> {
            if (toggle instanceof ToggleLabel l) {
                label.setText(l.getText());
            } else {
                label.setText(null);
            }
        });
        //snippet_1:end

        var box = new VBox(VGAP_10, seg, content);
        box.setAlignment(Pos.CENTER);

        return new ExampleBox(box, new Snippet(getClass(), 1));
    }

    private ExampleBox iconsExample() {
        //snippet_1:start
        var iconsSeg = new SegmentedControl(
            new ToggleLabel("Left", new FontIcon(randomIcon())),
            new ToggleLabel("Center", new FontIcon(randomIcon())),
            new ToggleLabel("Right", new FontIcon(randomIcon()))
        );

        var iconsOnlySeg = new SegmentedControl(
            new ToggleLabel("", new FontIcon(randomIcon())),
            new ToggleLabel("", new FontIcon(randomIcon())),
            new ToggleLabel("", new FontIcon(randomIcon()))
        );
        //snippet_1:end

        var box = new VBox(VGAP_10, iconsSeg, iconsOnlySeg);
        box.setAlignment(Pos.CENTER);

        return new ExampleBox(box, new Snippet(getClass(), 1));
    }
}
