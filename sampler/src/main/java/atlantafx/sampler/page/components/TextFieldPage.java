/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public final class TextFieldPage extends OutlinePage {

    public static final String NAME = "TextField";

    @Override
    public String getName() {
        return NAME;
    }

    public TextFieldPage() {
        super();

        addPageHeader();
        addFormattedText("""
            Text input component that allows a user to enter a single line of unformatted text."""
        );
        addSection("Usage", usageExample());
        addSection("Prompt Text", promptTextExample());
        addSection("Readonly", readonlyExample());
        addSection("Color", colorExample());
        addSection("Password Field", passwordFieldExample());
        addSection("Size", sizeExample());
        addSection("Rounded", roundedExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var tf1 = new TextField("Text");
        var tf2 = new TextField(FAKER.lorem().sentence(20));
        //snippet_1:end

        tf1.setPrefWidth(200);
        tf2.setPrefWidth(200);

        var box = new HBox(HGAP_20, tf1, tf2);
        var description = BBCodeParser.createFormattedText("""
            You create a text field control by creating an instance of the \
            [font=monospace]javafx.scene.control.TextField[/font] class."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox promptTextExample() {
        //snippet_2:start
        var tf = new TextField();
        tf.setPromptText("Prompt text");
        //snippet_2:end

        tf.setPrefWidth(200);

        var box = new HBox(tf);
        var description = BBCodeParser.createFormattedText("""
            The [i]TextField[/i] supports the notion of showing prompt text to the user when there \
            is no text already in the text field (either via the user, or set programmatically). \
            This is a useful way of informing the user as to what is expected in the text field, \
            without having to resort to tooltips or on-screen labels."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox readonlyExample() {
        //snippet_3:start
        var tf = new TextField("This text can't be modified");
        tf.setEditable(false);
        //snippet_3:end

        tf.setPrefWidth(200);

        var box = new HBox(tf);
        var description = BBCodeParser.createFormattedText("""
            [i]TextField[/i]'s [code]editable[/code] property indicates whether it can be edited by the user."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox colorExample() {
        //snippet_4:start
        var tf1 = new TextField("Text");
        tf1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);

        var tf2 = new TextField("Text");
        tf2.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        //snippet_4:end

        tf1.setPrefWidth(200);
        tf2.setPrefWidth(200);

        var box = new HBox(HGAP_20, tf1, tf2);
        var description = BBCodeParser.createFormattedText("""
            You can use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the [i]TextField[/i] color. This especially useful to indicate \
            the validation result."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ExampleBox passwordFieldExample() {
        //snippet_5:start
        var tf = new PasswordField();
        tf.setText("qwerty");
        //snippet_5:end

        tf.setPrefWidth(200);

        var box = new HBox(tf);
        var description = BBCodeParser.createFormattedText("""
            The [i]TextField[/i] flavor that masks entered characters. Note that password can \
            not be revealed. If you need this particular feature try [code]PasswordTextField[/code]. \
            It's the [code]CustomTextField[/code] flavor that does the same thing."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }

    private ExampleBox sizeExample() {
        //snippet_6:start
        var smallField = new TextField("Small");
        smallField.getStyleClass().add(Styles.SMALL);

        var normalField = new TextField("Normal");

        var largeField = new TextField("Large");
        largeField.getStyleClass().add(Styles.LARGE);
        //snippet_6:end

        smallField.setPrefWidth(70);
        normalField.setPrefWidth(120);
        largeField.setPrefWidth(200);

        var box = new HBox(HGAP_20, smallField, normalField, largeField);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Use [code]Styles.SMALL[/code] and [code]Styles.LARGE[/code] style classes \
            to change the [i]TextField[/i] size."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 6), description);
    }

    private ExampleBox roundedExample() {
        //snippet_7:start
        var smallField = new TextField("Small");
        smallField.getStyleClass().addAll(
            Styles.SMALL, Styles.ROUNDED
        );

        var normalField = new TextField("Normal");
        normalField.getStyleClass().addAll(Styles.ROUNDED);

        var largeField = new TextField("Large");
        largeField.getStyleClass().addAll(
            Styles.LARGE, Styles.ROUNDED
        );
        //snippet_7:end

        smallField.setPrefWidth(70);
        normalField.setPrefWidth(120);
        largeField.setPrefWidth(200);

        var box = new HBox(HGAP_20, smallField, normalField, largeField);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Use [code]Styles.ROUNDED[/code] style class to round the [i]TextField[/i] corners."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 7), description);
    }
}
