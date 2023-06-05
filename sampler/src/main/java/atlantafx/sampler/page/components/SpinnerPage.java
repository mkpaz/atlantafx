/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.base.util.IntegerStringConverter;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class SpinnerPage extends OutlinePage {

    public static final String NAME = "Spinner";

    @Override
    public String getName() {
        return NAME;
    }

    public SpinnerPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A single line text field that lets the user select a number or an object \
            value from an ordered sequence. The user may also be allowed to type a (legal) \
            value directly into the spinner."""
        );
        addSection("Usage", usageExample());
        addSection("Horizontal", horizontalExample());
        addSection("Vertical", verticalExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var spinner = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(spinner);
        spinner.setEditable(true);
        spinner.setPrefWidth(120);
        //snippet_1:end

        var box = new HBox(spinner);
        var description = BBCodeParser.createFormattedText("""
            A [i]Spinner[/i] has a [i]TextField[/i] child component that is responsible \
            for displaying and potentially changing the current value of the [i]Spinner[/i], \
            which is called the editor. By default the Spinner is non-editable, but input \
            can be accepted if the editable property is set to true. The [i]Spinner[/i] editor stays \
            in sync with the value factory by listening for changes to the value property \
            of the value factory."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox horizontalExample() {
        //snippet_2:start
        var sp1 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(sp1);
        sp1.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        sp1.setPrefWidth(120);
        sp1.setEditable(true);

        var sp2 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(sp2);
        sp2.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL);
        sp2.setPrefWidth(120);
        sp2.setEditable(true);

        var sp3 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(sp3);
        sp3.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        sp3.setPrefWidth(120);
        sp3.setEditable(true);

        var sp4 = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(sp4);
        sp4.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        sp4.setPrefWidth(120);
        sp4.setEditable(true);
        //snippet_2:end

        var grid = new GridPane();
        grid.setVgap(VGAP_20);
        grid.setHgap(HGAP_30);
        grid.addRow(0, captionLabel("STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL"), sp1);
        grid.addRow(1, captionLabel("STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL"), sp2);
        grid.addRow(2, captionLabel("STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL"), sp3);
        grid.addRow(3, captionLabel("STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL"), sp4);

        var description = BBCodeParser.createFormattedText("""
            The [i]Spinner[/i] also supports several style class modifiers that determine \
            the arrow’s position."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 2), description);
    }

    private ExampleBox verticalExample() {
        //snippet_3:start
        var sp = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(sp);
        sp.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
        sp.setEditable(true);
        sp.setPrefWidth(40);
        //snippet_3:end

        var box = new HBox(HGAP_30, captionLabel("STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL"), sp);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Similar to the previous example, but for the vertical direction."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }
}
