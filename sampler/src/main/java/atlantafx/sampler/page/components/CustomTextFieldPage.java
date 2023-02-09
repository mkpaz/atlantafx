/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.util.PasswordTextFormatter;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.Cursor;
import javafx.scene.layout.FlowPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class CustomTextFieldPage extends AbstractPage {

    public static final String NAME = "CustomTextField";
    private static final int PREF_WIDTH = 120;

    @Override
    public String getName() { return NAME; }

    public CustomTextFieldPage() {
        super();
        setUserContent(new FlowPane(
                PAGE_HGAP, PAGE_VGAP,
                leftIconSample(),
                rightIconSample(),
                bothIconsSample(),
                successSample(),
                dangerSample(),
                passwordSample()
        ));
    }

    private SampleBlock leftIconSample() {
        var tf = new CustomTextField();
        tf.setPromptText("Prompt text");
        tf.setRight(new FontIcon(Feather.X));
        tf.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Left", tf);
    }

    private SampleBlock rightIconSample() {
        var tf = new CustomTextField();
        tf.setPromptText("Prompt text");
        tf.setLeft(new FontIcon(Feather.MAP_PIN));
        tf.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Right", tf);
    }

    private SampleBlock bothIconsSample() {
        var tf = new CustomTextField("Text");
        tf.setLeft(new FontIcon(Feather.MAP_PIN));
        tf.setRight(new FontIcon(Feather.X));
        tf.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Both Sides", tf);
    }

    private SampleBlock successSample() {
        var tf = new CustomTextField("Text");
        tf.pseudoClassStateChanged(STATE_SUCCESS, true);
        tf.setRight(new FontIcon(Feather.X));
        tf.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Success", tf);
    }

    private SampleBlock dangerSample() {
        var tf = new CustomTextField();
        tf.pseudoClassStateChanged(STATE_DANGER, true);
        tf.setLeft(new FontIcon(Feather.MAP_PIN));
        tf.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Danger", tf);
    }

    private SampleBlock passwordSample() {
        var tf = new CustomTextField("qwerty");
        tf.setPrefWidth(PREF_WIDTH);

        var passwordFormatter = PasswordTextFormatter.create(tf);
        tf.setTextFormatter(passwordFormatter);

        var icon = new FontIcon(Feather.EYE_OFF);
        icon.setCursor(Cursor.HAND);
        icon.setOnMouseClicked(e -> {
            if (passwordFormatter.revealPasswordProperty().get()) {
                passwordFormatter.revealPasswordProperty().set(false);
                icon.setIconCode(Feather.EYE_OFF);
            } else {
                passwordFormatter.revealPasswordProperty().set(true);
                icon.setIconCode(Feather.EYE);
            }
        });
        tf.setRight(icon);

        return new SampleBlock("Password", tf);
    }
}
