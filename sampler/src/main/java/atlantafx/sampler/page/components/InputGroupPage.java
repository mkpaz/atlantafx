/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class InputGroupPage extends OutlinePage {

    public static final String NAME = "Input Group";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    public InputGroupPage() {
        super();

        addPageHeader();
        addFormattedText("""
            You can use the following utility classes: [code]Styles.LEFT_PILL[/code], \
            [code]Styles.CENTER_PILL[/code], and [code]Styles.RIGHT_PILL[/code] to combine \
            various input controls into input groups that allow them to appear as a single \
            control. This is entirely a CSS feature and does not require any additional wrappers."""
        );
        addSection("ComboBox", comboBoxExample());
        addSection("Button", buttonExample());
        addSection("Text Field", textFieldExample());
        addSection("MenuButton", menuButtonExample());
        addSection("Label", labelExample());
    }

    private ExampleBox comboBoxExample() {
        //snippet_1:start
        var leftCmb = new ComboBox<>();
        leftCmb.getItems().addAll("POST", "GET", "PUT", "PATCH", "DELETE");
        leftCmb.getStyleClass().add(Styles.LEFT_PILL);
        leftCmb.getSelectionModel().selectFirst();

        var rightTfd = new TextField("https://example.org");
        rightTfd.getStyleClass().add(Styles.RIGHT_PILL);
        HBox.setHgrow(rightTfd, Priority.ALWAYS);
        //snippet_1:end

        var box = new HBox(leftCmb, rightTfd);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(400);
        box.setMaxWidth(400);

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how a [i]ComboBox[/i] can be combined \
            with a [i]TextField[/i].""");

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox buttonExample() {
        //snippet_2:start
        var leftTfd = new TextField();
        leftTfd.setText(FAKER.internet().password());
        leftTfd.getStyleClass().add(Styles.LEFT_PILL);
        HBox.setHgrow(leftTfd, Priority.ALWAYS);

        var rightBtn = new Button(
            "", new FontIcon(Feather.REFRESH_CW)
        );
        rightBtn.getStyleClass().addAll(Styles.BUTTON_ICON);
        rightBtn.setOnAction(
            e -> leftTfd.setText(FAKER.internet().password())
        );
        rightBtn.getStyleClass().add(Styles.RIGHT_PILL);
        //snippet_2:end

        var box = new HBox(leftTfd, rightBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(400);
        box.setMaxWidth(400);

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how a [i]Button[/i] can be combined \
            with a [i]TextField[/i].""");

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox textFieldExample() {
        //snippet_3:start
        var leftTfd = new TextField("192.168.1.10");
        leftTfd.getStyleClass().add(Styles.LEFT_PILL);

        var centerTfd = new TextField("24");
        centerTfd.getStyleClass().add(Styles.CENTER_PILL);
        centerTfd.setPrefWidth(70);

        var rightTfd = new TextField("192.168.1.1");
        rightTfd.getStyleClass().add(Styles.RIGHT_PILL);
        //snippet_3:end

        var box = new HBox(leftTfd, centerTfd, rightTfd);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(400);
        box.setMaxWidth(400);

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how a multiple [i]TextField[/i]'s can be \
            combined into a input group.""");

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox menuButtonExample() {
        //snippet_4:start
        var rightTfd = new TextField(FAKER.harryPotter().spell());
        rightTfd.getStyleClass().add(Styles.RIGHT_PILL);
        HBox.setHgrow(rightTfd, Priority.ALWAYS);

        var spellItem = new MenuItem("Spell");
        spellItem.setOnAction(
            e -> rightTfd.setText(FAKER.harryPotter().spell())
        );

        var characterItem = new MenuItem("Character");
        characterItem.setOnAction(
            e -> rightTfd.setText(FAKER.harryPotter().character())
        );

        var locationItem = new MenuItem("Location");
        locationItem.setOnAction(
            e -> rightTfd.setText(FAKER.harryPotter().location())
        );

        var leftMenu = new MenuButton("Dropdown");
        leftMenu.getItems().addAll(spellItem, characterItem, locationItem);
        leftMenu.getStyleClass().add(Styles.LEFT_PILL);
        //snippet_4:end

        var box = new HBox(leftMenu, rightTfd);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(400);
        box.setMaxWidth(400);

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how a [i]MenuButton[/i] can be combined \
            with a [i]TextField[/i].""");

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ExampleBox labelExample() {
        //snippet_5:start
        var leftLbl1 = new Label("", new CheckBox());
        leftLbl1.getStyleClass().add(Styles.LEFT_PILL);

        var rightTfd1 = new TextField();
        rightTfd1.setPromptText("Username");
        rightTfd1.getStyleClass().add(Styles.RIGHT_PILL);
        HBox.setHgrow(rightTfd1, Priority.ALWAYS);

        var sample1 = new HBox(leftLbl1, rightTfd1);
        sample1.setAlignment(Pos.CENTER_LEFT);

        // ~
        var leftTfd2 = new TextField("johndoe");
        leftTfd2.getStyleClass().add(Styles.LEFT_PILL);
        HBox.setHgrow(leftTfd2, Priority.ALWAYS);

        var centerLbl2 = new Label("@");
        centerLbl2.setMinWidth(50);
        centerLbl2.setAlignment(Pos.CENTER);
        centerLbl2.getStyleClass().add(Styles.CENTER_PILL);

        var rightTfd2 = new TextField("gmail.com");
        rightTfd2.getStyleClass().add(Styles.RIGHT_PILL);
        HBox.setHgrow(rightTfd2, Priority.ALWAYS);

        var sample2 = new HBox(leftTfd2, centerLbl2, rightTfd2);
        sample2.setAlignment(Pos.CENTER_LEFT);

        // ~
        var leftTfd3 = new TextField("+123456");
        leftTfd3.getStyleClass().add(Styles.LEFT_PILL);
        HBox.setHgrow(leftTfd3, Priority.ALWAYS);

        var rightLbl3 = new Label("", new FontIcon(Feather.DOLLAR_SIGN));
        rightLbl3.getStyleClass().add(Styles.RIGHT_PILL);

        var sample3 = new HBox(leftTfd3, rightLbl3);
        sample3.setAlignment(Pos.CENTER_LEFT);
        //snippet_5:end

        sample1.setMinWidth(400);
        sample1.setMaxWidth(400);

        sample2.setMinWidth(400);
        sample2.setMaxWidth(400);

        sample3.setMinWidth(400);
        sample3.setMaxWidth(400);

        var box = new VBox(VGAP_20, sample1, sample2, sample3);

        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how a [i]Label[/i] can be used \
            in combination with various controls.""");

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }
}

