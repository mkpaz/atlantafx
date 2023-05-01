/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.MaskTextField;
import atlantafx.base.controls.PasswordTextField;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public class CustomTextFieldPage extends AbstractPage {

    public static final String NAME = "CustomTextField";
    private static final int PREF_WIDTH = 120;

    @Override
    public String getName() {
        return NAME;
    }

    public CustomTextFieldPage() {
        super();
        setUserContent(new FlowPane(
            PAGE_HGAP, PAGE_VGAP,
            leftIconSample(),
            rightIconSample(),
            bothIconsSample(),
            successSample(),
            dangerSample(),
            passwordSample(),
            maskSample()
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
        var tf = new PasswordTextField("qwerty");
        tf.setPrefWidth(PREF_WIDTH);

        var icon = new FontIcon(Feather.EYE_OFF);
        icon.setCursor(Cursor.HAND);
        icon.setOnMouseClicked(e -> {
            if (tf.revealPasswordProperty().get()) {
                tf.revealPasswordProperty().set(false);
                icon.setIconCode(Feather.EYE_OFF);
            } else {
                tf.revealPasswordProperty().set(true);
                icon.setIconCode(Feather.EYE);
            }
        });
        tf.setRight(icon);

        return new SampleBlock("Password", tf);
    }

    private SampleBlock maskSample() {
        var phoneField = new MaskTextField("(999) 999 99 99");
        phoneField.setPromptText("(999) 999 99 99");
        phoneField.setLeft(new FontIcon(Material2OutlinedMZ.PHONE));
        phoneField.setPrefWidth(180);

        var cardField = new MaskTextField("9999-9999-9999-9999");
        cardField.setLeft(new FontIcon(Material2OutlinedAL.CREDIT_CARD));
        cardField.setPrefWidth(200);

        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        var timeField = new MaskTextField("29:59");
        timeField.setText(LocalTime.now(ZoneId.systemDefault()).format(timeFormatter));
        timeField.setLeft(new FontIcon(Material2OutlinedMZ.TIMER));
        timeField.setPrefWidth(120);
        timeField.textProperty().addListener((obs, old, val) -> {
            if (val != null) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    LocalTime.parse(val, timeFormatter);
                    timeField.pseudoClassStateChanged(STATE_DANGER, false);
                } catch (DateTimeParseException e) {
                    timeField.pseudoClassStateChanged(STATE_DANGER, true);
                }
            }
        });

        var content = new HBox(
            BLOCK_HGAP,
            new VBox(5, new Label("Phone Number"), phoneField),
            new VBox(5, new Label("Bank Card"), cardField),
            new VBox(5, new Label("Time"), timeField)
        );
        content.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Input Mask", content);
    }
}
