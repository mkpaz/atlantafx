/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;

public class TextFieldPage extends AbstractPage {

    public static final String NAME = "TextField";

    @Override
    public String getName() { return NAME; }

    public TextFieldPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(samples());
    }

    private FlowPane samples() {
        var basicField = new TextField("Text");
        var basicBlock = new SampleBlock("Basic", basicField);

        var passwordField = new PasswordField();
        passwordField.setText("qwerty");
        var passwordBlock = new SampleBlock("Password", passwordField);

        var promptField = new TextField();
        promptField.setPromptText("Prompt text");
        var promptBlock = new SampleBlock("Prompt", promptField);

        var readonlyField = new TextField("Text");
        readonlyField.setEditable(false);
        var readonlyBlock = new SampleBlock("Readonly", readonlyField);

        var disabledField = new TextField("Text");
        disabledField.setDisable(true);
        var disabledBlock = new SampleBlock("Disabled", disabledField);

        var successField = new TextField("Text");
        successField.pseudoClassStateChanged(STATE_SUCCESS, true);
        var successBlock = new SampleBlock("Success", successField);

        var dangerField = new TextField("Text");
        dangerField.pseudoClassStateChanged(STATE_DANGER, true);
        var dangerBlock = new SampleBlock("Danger", dangerField);

        var roundedField = new TextField("Text");
        roundedField.getStyleClass().add(Styles.ROUNDED);
        var roundedBlock = new SampleBlock("Rounded", roundedField);

        var flowPane = new FlowPane(20, 20);
        flowPane.getChildren().setAll(
                basicBlock.getRoot(),
                passwordBlock.getRoot(),
                promptBlock.getRoot(),
                readonlyBlock.getRoot(),
                disabledBlock.getRoot(),
                successBlock.getRoot(),
                dangerBlock.getRoot(),
                roundedBlock.getRoot()
        );

        return flowPane;
    }
}
