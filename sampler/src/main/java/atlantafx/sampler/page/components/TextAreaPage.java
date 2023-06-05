/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public final class TextAreaPage extends OutlinePage {

    public static final String NAME = "TextArea";

    @Override
    public String getName() {
        return NAME;
    }

    public TextAreaPage() {
        super();

        addPageHeader();
        addFormattedText(
            "Text input component that allows a user to enter multiple lines of plain text."
        );
        addSection("Usage", usageExample());
        addSection("Prompt Text", promptTextExample());
        addSection("Readonly", readonlyExample());
        addSection("Color", colorExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var ta1 = new TextArea("Text");

        var ta2 = new TextArea(
            Stream.generate(() -> FAKER.lorem().paragraph())
                .limit(10)
                .collect(Collectors.joining("\n"))
        );
        ta2.setWrapText(true);
        //snippet_1:end

        ta1.setMinSize(300, 120);
        ta1.setMaxSize(300, 120);

        ta2.setMinSize(300, 120);
        ta2.setMaxSize(300, 120);

        var box = new HBox(HGAP_20, ta1, ta2);
        var description = BBCodeParser.createFormattedText("""
            You create a text area control by creating an instance of the \
            [font=monospace]javafx.scene.control.TextArea[/font] class. \
            By default long text won't be wrapped. You should set [code]setWrapText(true)[/code] \
            to use this feature."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox promptTextExample() {
        //snippet_2:start
        var ta = new TextArea();
        ta.setPromptText("Prompt text");
        ta.setWrapText(true);
        //snippet_2:end

        ta.setMaxSize(300, 120);
        ta.setMinSize(300, 120);

        var box = new HBox(ta);
        var description = BBCodeParser.createFormattedText("""
            The [i]TextArea[/i] supports the notion of showing prompt text to the user when there \
            is no text already in the text area (either via the user, or set programmatically). \
            This is a useful way of informing the user as to what is expected in the [i]TextArea[/i], \
            without having to resort to tooltips or on-screen labels."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox readonlyExample() {
        //snippet_3:start
        var ta = new TextArea("This text can't be modified");
        ta.setWrapText(true);
        ta.setEditable(false);
        //snippet_3:end

        ta.setMaxSize(300, 120);
        ta.setMinSize(300, 120);

        var box = new HBox(ta);
        var description = BBCodeParser.createFormattedText("""
            The [i]TextArea[/i]'s [code]editable[/code] property indicates whether it \
            can be edited by the user."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox colorExample() {
        //snippet_4:start
        var ta1 = new TextArea("Text");
        ta1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        ta1.setWrapText(true);

        var ta2 = new TextArea("Text");
        ta2.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        ta2.setWrapText(true);
        //snippet_4:end

        ta1.setMaxSize(300, 120);
        ta1.setMinSize(300, 120);

        ta2.setMaxSize(300, 120);
        ta2.setMinSize(300, 120);

        var box = new HBox(HGAP_20, ta1, ta2);
        var description = BBCodeParser.createFormattedText("""
            You can use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the [i]TextArea[/i] color. This especially useful to indicate \
            the validation result."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
