/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static javafx.scene.control.Alert.AlertType;
import static javafx.scene.control.ButtonBar.ButtonData;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class DialogPage extends OutlinePage {

    public static final String NAME = "Dialog";

    @Override
    public String getName() {
        return NAME;
    }

    public DialogPage() {
        super();

        addPageHeader();
        addFormattedText("""
            Dialog is a user interface component that allows to create dialog windows \
            that can be used to prompt users for information or to display messages or warnings."""
        );
        addSection("Notifications", notificationDialogExample());
        addSection("Exception Dialog", exceptionDialogExample());
        addSection("Confirmation Dialog", confirmationDialogExample());
        addSection("Text Input Dialog", textInputDialogExample());
        addSection("Choice Dialog", choiceDialogExample());
        addSection("No Header", notificationNoHeaderDialogExample());
    }

    private ExampleBox notificationDialogExample() {
        //snippet_1:start
        var infoBtn = new Button("Info", new FontIcon(Feather.INFO));
        infoBtn.setOnAction(e -> {
            var alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(FAKER.chuckNorris().fact());
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            show(alert);
        });

        var warnBtn = new Button("Click", new FontIcon(Feather.ALERT_TRIANGLE));
        warnBtn.setOnAction(e -> {
            var alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText(FAKER.chuckNorris().fact());
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            show(alert);
        });

        var errorBtn = new Button("Click", new FontIcon(Feather.X_CIRCLE));
        errorBtn.setOnAction(e -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(FAKER.chuckNorris().fact());
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            show(alert);
        });
        //snippet_1:end

        var box = new HBox(30, infoBtn, warnBtn, errorBtn);
        var description = BBCodeParser.createFormattedText("""
            Pre-built dialog types for displaying information, warnings, and errors."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox exceptionDialogExample() {
        //snippet_2:start
        var button = new Button("Click", new FontIcon(Feather.MEH));
        button.setOnAction(e -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText(FAKER.chuckNorris().fact());
            alert.setContentText(FAKER.lorem().paragraph(3));

            var exception = new RuntimeException(FAKER.chuckNorris().fact());

            var stringWriter = new StringWriter();
            var printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);

            var textArea = new TextArea(stringWriter.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            var content = new GridPane();
            content.setMaxWidth(Double.MAX_VALUE);
            content.add(new Label("Full stacktrace:"), 0, 0);
            content.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(content);
            alert.initOwner(getScene().getWindow());
            show(alert);
        });
        //snippet_2:end

        var description = BBCodeParser.createFormattedText("""
            A custom dialog that is designed to display information about exceptions that \
            are thrown in JavaFX applications."""
        );

        var example = new ExampleBox(new HBox(button), new Snippet(getClass(), 2), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox confirmationDialogExample() {
        //snippet_3:start
        var button = new Button(
            "Click", new FontIcon(Feather.CHECK_SQUARE)
        );
        button.setOnAction(e -> {
            var alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(FAKER.chuckNorris().fact());
            alert.setContentText(FAKER.lorem().paragraph(3));

            ButtonType yesBtn = new ButtonType("Yes", ButtonData.YES);
            ButtonType noBtn = new ButtonType("No", ButtonData.NO);
            ButtonType cancelBtn = new ButtonType(
                "Cancel", ButtonData.CANCEL_CLOSE
            );

            alert.getButtonTypes().setAll(yesBtn, noBtn, cancelBtn);
            alert.initOwner(getScene().getWindow());
            show(alert);
        });
        //snippet_3:end

        var description = BBCodeParser.createFormattedText("""
            The confirmation alert type configures the [i]Alert[/i] dialog to appear in a way that \
            suggests the content of the dialog is seeking confirmation from the user."""
        );

        var example = new ExampleBox(new HBox(button), new Snippet(getClass(), 3), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox textInputDialogExample() {
        //snippet_4:start
        var button = new Button("Click", new FontIcon(Feather.EDIT_2));
        button.setOnAction(e -> {
            var dialog = new TextInputDialog();
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText(FAKER.chuckNorris().fact());
            dialog.setContentText("Enter your name:");
            dialog.initOwner(getScene().getWindow());
            show(dialog);
        });
        //snippet_4:end

        var description = BBCodeParser.createFormattedText(
            "A dialog that shows a text input control to the user."
        );

        var example = new ExampleBox(new HBox(button), new Snippet(getClass(), 4), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox choiceDialogExample() {
        //snippet_5:start
        var button = new Button("Click", new FontIcon(Feather.LIST));
        button.setOnAction(e -> {
            var choices = List.of("A", "B", "C");
            var dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choice Dialog");
            dialog.setHeaderText(FAKER.chuckNorris().fact());
            dialog.setContentText("Choose your letter:");
            dialog.initOwner(getScene().getWindow());
            show(dialog);
        });
        //snippet_5:end

        var description = BBCodeParser.createFormattedText("""
            A dialog that shows a list of choices to the user, from which they can pick one item at most."""
        );

        var example = new ExampleBox(new HBox(button), new Snippet(getClass(), 5), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox notificationNoHeaderDialogExample() {
        //snippet_6:start
        var infoBtn = new Button("Info", new FontIcon(Feather.INFO));
        infoBtn.setOnAction(e -> {
            var alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            show(alert);
        });

        var warnBtn = new Button("Click", new FontIcon(Feather.ALERT_TRIANGLE));
        warnBtn.setOnAction(e -> {
            var alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText(null);
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            show(alert);
        });

        var errorBtn = new Button("Click", new FontIcon(Feather.X_CIRCLE));
        errorBtn.setOnAction(e -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            show(alert);
        });
        //snippet_6:end

        var box = new HBox(30, infoBtn, warnBtn, errorBtn);
        var description = BBCodeParser.createFormattedText(
            "The header text can be hidden."
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 6), description);
        example.setAllowDisable(false);

        return example;
    }

    private void show(Dialog<?> alert) {
        // copy customized styles, like changed accent color etc
        try {
            for (var pc : getScene().getRoot().getPseudoClassStates()) {
                alert.getDialogPane().pseudoClassStateChanged(pc, true);
            }
            alert.getDialogPane().getStylesheets().addAll(getScene().getRoot().getStylesheets());
        } catch (Exception ignored) {
            // yes, ignored
        }

        alert.showAndWait();
    }
}
