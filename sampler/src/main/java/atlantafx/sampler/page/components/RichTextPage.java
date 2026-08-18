/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RichTextPage extends OutlinePage {

    public static final String NAME = "RichTextArea";

    private static final StyleAttributeMap BOLD = StyleAttributeMap.builder()
        .setBold(true)
        .build();
    private static final StyleAttributeMap COLOR = StyleAttributeMap.builder()
        .setTextColor(Color.RED)
        .build();

    @Override
    public String getName() {
        return NAME;
    }

    public RichTextPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The RichTextArea control is designed for visualizing and editing rich text
            that can be styled in a variety of ways. This is an [b]incubator module[/b], meaning it's not
            officially released yet."""
        );
        addSection("Usage", usageExample());
        addSection("Readonly", readonlyExample());
        addSection("Color", colorExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var rta1 = new RichTextArea();
        rta1.appendText("A");
        rta1.appendText(" styled", BOLD);
        rta1.appendText(" text.", COLOR);

        var rta2 = new RichTextArea();
        rta2.appendText(Stream.generate(() -> FAKER.lorem().paragraph())
            .limit(10)
            .collect(Collectors.joining("\n"))
        );
        rta2.setWrapText(true);
        //snippet_1:end

        rta1.setMinSize(300, 120);
        rta1.setMaxSize(300, 120);

        rta2.setMinSize(300, 120);
        rta2.setMaxSize(300, 120);

        var box = new HBox(HGAP_20, rta1, rta2);
        var description = BBCodeParser.createFormattedText("""
            You create a rich text area control by creating an instance of the \
            [font=monospace]jfx.incubator.scene.control.richtext.RichTextArea[/font] class. \
            By default long text won't be wrapped. You should set [code]setWrapText(true)[/code] \
            to use this feature."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox readonlyExample() {
        //snippet_3:start
        var rta = new RichTextArea();
        rta.appendText("This text");
        rta.appendText(" can't be", BOLD);
        rta.appendText(" modified.", COLOR);
        rta.setWrapText(true);
        rta.setEditable(false);
        //snippet_3:end

        rta.setMaxSize(300, 120);
        rta.setMinSize(300, 120);

        var box = new HBox(rta);
        var description = BBCodeParser.createFormattedText("""
            The [i]RichTextArea[/i]'s [code]editable[/code] property indicates whether it \
            can be edited by the user."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox colorExample() {
        //snippet_4:start
        var rta1 = new RichTextArea();
        rta1.appendText("A");
        rta1.appendText(" styled", BOLD);
        rta1.appendText(" text.", COLOR);
        rta1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        rta1.setWrapText(true);

        var rta2 = new RichTextArea();
        rta2.appendText("A");
        rta2.appendText(" styled", BOLD);
        rta2.appendText(" text.", COLOR);
        rta2.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        rta2.setWrapText(true);
        //snippet_4:end

        rta1.setMaxSize(300, 120);
        rta1.setMinSize(300, 120);

        rta2.setMaxSize(300, 120);
        rta2.setMinSize(300, 120);

        var box = new HBox(HGAP_20, rta1, rta2);
        var description = BBCodeParser.createFormattedText("""
            You can use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the [i]RichTextArea[/i] color. This especially useful to indicate \
            the validation result."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
