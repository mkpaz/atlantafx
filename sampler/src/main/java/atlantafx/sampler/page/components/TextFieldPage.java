/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.LARGE;
import static atlantafx.base.theme.Styles.ROUNDED;
import static atlantafx.base.theme.Styles.SMALL;
import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TextFieldPage extends AbstractPage {

    public static final String NAME = "TextField";

    @Override
    public String getName() {
        return NAME;
    }

    public TextFieldPage() {
        super();
        setUserContent(new VBox(
            PAGE_VGAP,
            expandingHBox(basicSample(), promptSample(), passwordSample()),
            expandingHBox(readonlySample(), successSample(), dangerSample()),
            expandingHBox(sizeSample(), roundedSample()),
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

    private SampleBlock sizeSample() {
        var smallField = new TextField("Small");
        smallField.getStyleClass().add(SMALL);
        smallField.setPrefWidth(70);

        var normalField = new TextField("Normal");
        normalField.setPrefWidth(120);

        var largeField = new TextField("Large");
        largeField.getStyleClass().add(LARGE);
        largeField.setPrefWidth(200);

        var content = new HBox(BLOCK_HGAP, smallField, normalField, largeField);
        content.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Size", content);
    }

    private SampleBlock roundedSample() {
        var field = new TextField("Text");
        field.getStyleClass().add(ROUNDED);
        return new SampleBlock("Rounded", field);
    }

    private SampleBlock disabledSample() {
        var field = new TextField("Text");
        field.setDisable(true);

        var block = new SampleBlock("Disabled", field);
        block.setMaxWidth(250);

        return block;
    }
}
