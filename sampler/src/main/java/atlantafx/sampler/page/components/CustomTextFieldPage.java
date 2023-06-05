/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.MaskTextField;
import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public final class CustomTextFieldPage extends OutlinePage {

    public static final String NAME = "CustomTextField";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public CustomTextFieldPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A base class for placing nodes inside the text field itself, without being \
            on top of the users typed-in text."""
        );
        addSection("Usage", usageExample());
        addSection("Color", colorExample());
        addSection("Password", passwordSample());
        addSection("Input Mask", inputMaskExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var tf1 = new CustomTextField();
        tf1.setPromptText("Prompt text");
        tf1.setRight(new FontIcon(Feather.X));
        tf1.setPrefWidth(150);

        var tf2 = new CustomTextField();
        tf2.setPromptText("Prompt text");
        tf2.setLeft(new FontIcon(Feather.MAP_PIN));
        tf2.setPrefWidth(150);

        var tf3 = new CustomTextField("Text");
        tf3.setLeft(new FontIcon(Feather.MAP_PIN));
        tf3.setRight(new FontIcon(Feather.X));
        tf3.setPrefWidth(150);
        //snippet_1:end

        var box = new HBox(30, tf1, tf2, tf3);
        var description = BBCodeParser.createFormattedText("""
            You can add arbitrary nodes to the [i]CustomTextField[/i] by setting \
            the [code]left[/code] and [code]right[/code] properties, respectively."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox colorExample() {
        //snippet_2:start
        var tf1 = new CustomTextField("Text");
        tf1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        tf1.setRight(new FontIcon(Feather.X));
        tf1.setPrefWidth(150);

        var tf2 = new CustomTextField();
        tf2.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        tf2.setLeft(new FontIcon(Feather.MAP_PIN));
        tf2.setPrefWidth(150);
        //snippet_2:end

        var box = new HBox(30, tf1, tf2);
        var description = BBCodeParser.createFormattedText("""
            Use [code]Styles.STATE_SUCCESS[/code] or [code]Styles.STATE_DANGER[/code] \
            pseudo-classes to change the CustomTextField/i] color. This especially useful to indicate \
            the validation result."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox passwordSample() {
        //snippet_3:start
        var tf = new PasswordTextField("qwerty");
        tf.setPrefWidth(250);

        var icon = new FontIcon(Feather.EYE_OFF);
        icon.setCursor(Cursor.HAND);
        icon.setOnMouseClicked(e -> {
            icon.setIconCode(tf.getRevealPassword()
                ? Feather.EYE_OFF : Feather.EYE
            );
            tf.setRevealPassword(!tf.getRevealPassword());
        });
        tf.setRight(icon);
        //snippet_3:end

        var box = new HBox(30, tf);
        var description = BBCodeParser.createFormattedText("""
            The [i]PasswordTextField[/i] is a variant of [i]CustomTextField[/i] that allows users \
            to input passwords. Unlike the standard JavaFX [i]PasswordField[/i], the content of \
            the [i]PasswordTextField[/i] can be revealed."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox inputMaskExample() {
        //snippet_4:start
        var phoneField = new MaskTextField("(999) 999 99 99");
        phoneField.setPromptText("(999) 999 99 99");
        phoneField.setLeft(new FontIcon(Material2OutlinedMZ.PHONE));
        phoneField.setPrefWidth(180);

        var cardField = new MaskTextField("9999-9999-9999-9999");
        cardField.setLeft(new FontIcon(Material2OutlinedAL.CREDIT_CARD));
        cardField.setPrefWidth(200);

        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        var timeField = new MaskTextField("29:59");
        timeField.setText(
            LocalTime.now(ZoneId.systemDefault()).format(timeFormatter)
        );
        timeField.setLeft(new FontIcon(Material2OutlinedMZ.TIMER));
        timeField.setPrefWidth(120);
        timeField.textProperty().addListener((obs, old, val) -> {
            if (val != null) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    LocalTime.parse(val, timeFormatter);
                    timeField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                } catch (DateTimeParseException e) {
                    timeField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                }
            }
        });
        //snippet_4:end

        var box = new HBox(
            HGAP_20,
            new VBox(5, new Label("Phone Number"), phoneField),
            new VBox(5, new Label("Bank Card"), cardField),
            new VBox(5, new Label("Time"), timeField)
        );
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The [i]MaskTextField[/i] allows to restrict user input by applying a \
            position-based mask. This is useful for editing cases where the input \
            string has a fixed length and each character can be restricted based on its position."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
