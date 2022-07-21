/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.controls.Spacer;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import static javafx.scene.control.Alert.AlertType;
import static javafx.scene.control.ButtonBar.ButtonData;

public class DialogPage extends AbstractPage {

    public static final String NAME = "Dialog";

    @Override
    public String getName() { return NAME; }

    private final BooleanProperty showHeaderProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty minDecorationsProperty = new SimpleBooleanProperty(true);

    public DialogPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(playground());
    }

    private VBox playground() {
        var showHeaderToggle = new ToggleSwitch("Show header");
        showHeaderProperty.bind(showHeaderToggle.selectedProperty());
        showHeaderToggle.setSelected(true);

        var minDecorationsToggle = new ToggleSwitch("Minimum decorations");
        minDecorationsProperty.bind(minDecorationsToggle.selectedProperty());
        minDecorationsToggle.setSelected(true);

        var controls = new HBox(20, new Spacer(), showHeaderToggle, minDecorationsToggle, new Spacer());
        controls.setAlignment(Pos.CENTER);

        // ~

        var row1 = new HBox(40, infoDialogButton().getRoot(), warnDialogButton().getRoot(), errorDialogButton().getRoot());

        var row2 = new HBox(40, exceptionDialogButton().getRoot(), confirmationDialogButton().getRoot(), textInputDialogButton().getRoot(), choiceDialogButton().getRoot());

        var playground = new VBox(20);
        playground.setMinHeight(100);
        playground.getChildren().setAll(controls, new Separator(Orientation.HORIZONTAL), row1, row2);

        return playground;
    }

    private SampleBlock infoDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.INFO));
        button.setOnAction(e -> {
            var alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(randomHeader());
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            alert.initStyle(getModality());
            alert.showAndWait();
        });

        return new SampleBlock("Information", button);
    }

    private SampleBlock warnDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.ALERT_TRIANGLE));
        button.setOnAction(e -> {
            var alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText(randomHeader());
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            alert.initStyle(getModality());
            alert.showAndWait();
        });

        return new SampleBlock("Warning", button);
    }

    private SampleBlock errorDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.X_CIRCLE));
        button.setOnAction(e -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(randomHeader());
            alert.setContentText(FAKER.lorem().paragraph(3));
            alert.initOwner(getScene().getWindow());
            alert.initStyle(getModality());
            alert.showAndWait();
        });

        return new SampleBlock("Error", button);
    }

    private SampleBlock exceptionDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.MEH));

        button.setOnAction(e -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(randomHeader());
            alert.setContentText(FAKER.lorem().paragraph(3));

            var exception = new RuntimeException(FAKER.chuckNorris().fact());

            var stringWriter = new StringWriter();
            var printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);

            var label = new Label("Full stacktrace:");

            var textArea = new TextArea(stringWriter.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            var content = new GridPane();
            content.setMaxWidth(Double.MAX_VALUE);
            content.add(label, 0, 0);
            content.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(content);
            alert.initOwner(getScene().getWindow());
            alert.initStyle(getModality());
            alert.showAndWait();
        });

        return new SampleBlock("Exception", button);
    }

    private SampleBlock confirmationDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.CHECK_SQUARE));

        button.setOnAction(e -> {
            var alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(randomHeader());
            alert.setContentText(FAKER.lorem().paragraph(3));

            ButtonType yesBtn = new ButtonType("Yes", ButtonData.YES);
            ButtonType noBtn = new ButtonType("No", ButtonData.NO);
            ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(yesBtn, noBtn, cancelBtn);

            alert.initOwner(getScene().getWindow());
            alert.initStyle(getModality());
            alert.showAndWait();
        });

        return new SampleBlock("Confirmation", button);
    }

    private SampleBlock textInputDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.EDIT_2));

        button.setOnAction(e -> {
            var dialog = new TextInputDialog();
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText(randomHeader());
            dialog.setContentText("Enter your name:");
            dialog.initOwner(getScene().getWindow());
            dialog.initStyle(getModality());
            dialog.showAndWait();
        });

        return new SampleBlock("Text Input", button);
    }

    private SampleBlock choiceDialogButton() {
        var button = new Button("Click");
        button.setGraphic(new FontIcon(Feather.LIST));

        button.setOnAction(e -> {
            var choices = new ArrayList<>();
            choices.add("A");
            choices.add("B");
            choices.add("C");

            var dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choice Dialog");
            dialog.setHeaderText(randomHeader());
            dialog.setContentText("Choose your letter:");
            dialog.initOwner(getScene().getWindow());
            dialog.initStyle(getModality());
            dialog.showAndWait();
        });

        return new SampleBlock("Choice", button);
    }

    private String randomHeader() {
        return showHeaderProperty.get() ? FAKER.chuckNorris().fact() : null;
    }

    private StageStyle getModality() {
        return minDecorationsProperty.get() ? StageStyle.UTILITY : StageStyle.DECORATED;
    }
}
