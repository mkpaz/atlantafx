/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static javafx.scene.control.Alert.AlertType;
import static javafx.scene.control.ButtonBar.ButtonData;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class DialogPage extends AbstractPage {

    public static final String NAME = "Dialog";

    @Override
    public String getName() {
        return NAME;
    }

    private final BooleanProperty showHeaderProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty minDecorationsProperty = new SimpleBooleanProperty(true);

    public DialogPage() {
        super();
        createView();
    }

    private void createView() {
        var showHeaderToggle = new ToggleSwitch("Show header");
        showHeaderProperty.bind(showHeaderToggle.selectedProperty());
        showHeaderToggle.setSelected(true);

        var minDecorationsToggle = new ToggleSwitch("Minimum decorations");
        minDecorationsProperty.bind(minDecorationsToggle.selectedProperty());
        minDecorationsToggle.setSelected(true);

        var controls = new HBox(BLOCK_HGAP, showHeaderToggle, minDecorationsToggle);
        controls.setAlignment(Pos.CENTER);

        var samples = new FlowPane(
            PAGE_HGAP, PAGE_VGAP,
            infoDialogSample(),
            warningDialogSample(),
            errorDialogSample(),
            exceptionDialogSample(),
            confirmationDialogSample(),
            textInputDialogSample(),
            choiceDialogSample()
        );

        setUserContent(new VBox(
            10,
            controls,
            new Separator(Orientation.HORIZONTAL),
            samples
        ));
    }

    private SampleBlock infoDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.INFO));
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

    private SampleBlock warningDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.ALERT_TRIANGLE));
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

    private SampleBlock errorDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.X_CIRCLE));
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

    private SampleBlock exceptionDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.MEH));
        button.setOnAction(e -> {
            var alert = new Alert(AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText(randomHeader());
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
            alert.initStyle(getModality());
            alert.showAndWait();
        });

        return new SampleBlock("Exception", button);
    }

    private SampleBlock confirmationDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.CHECK_SQUARE));
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

    private SampleBlock textInputDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.EDIT_2));
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

    private SampleBlock choiceDialogSample() {
        var button = new Button("Click", new FontIcon(Feather.LIST));
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
