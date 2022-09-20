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
        setUserContent(new FlowPane(
                PAGE_HGAP, PAGE_VGAP,
                basicSample(),
                promptSample(),
                passwordSample(),
                readonlySample(),
                successSample(),
                dangerSample(),
                roundedSample(),
                disabledSample()
        ));
    }

    private SampleBlock basicSample() {
        var field = new TextField("Text");
        return new SampleBlock("Basic", field);
    }

    private SampleBlock passwordSample() {
        var field = new PasswordField();
        field.setText("qwerty");
        return new SampleBlock("Password", field);
    }

    private SampleBlock promptSample() {
        var field = new TextField();
        field.setPromptText("Prompt text");
        return new SampleBlock("Prompt", field);
    }

    private SampleBlock readonlySample() {
        var field = new TextField("Text");
        field.setEditable(false);
        return new SampleBlock("Readonly", field);
    }

    private SampleBlock successSample() {
        var field = new TextField("Text");
        field.pseudoClassStateChanged(STATE_SUCCESS, true);
        return new SampleBlock("Success", field);
    }

    private SampleBlock dangerSample() {
        var field = new TextField("Text");
        field.pseudoClassStateChanged(STATE_DANGER, true);
        return new SampleBlock("Danger", field);
    }

    private SampleBlock roundedSample() {
        var field = new TextField("Text");
        field.getStyleClass().add(Styles.ROUNDED);
        return new SampleBlock("Rounded", field);
    }

    private SampleBlock disabledSample() {
        var field = new TextField("Text");
        field.setDisable(true);
        return new SampleBlock("Disabled", field);
    }
}
