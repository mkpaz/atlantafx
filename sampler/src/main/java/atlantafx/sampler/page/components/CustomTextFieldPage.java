/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.CustomTextField;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.layout.FlowPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;

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
                dangerSample()
        ));
    }

    private SampleBlock leftIconSample() {
        var leftIconField = new CustomTextField();
        leftIconField.setPromptText("Prompt text");
        leftIconField.setRight(new FontIcon(Feather.X));
        leftIconField.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Left", leftIconField);
    }

    private SampleBlock rightIconSample() {
        var rightIconField = new CustomTextField();
        rightIconField.setPromptText("Prompt text");
        rightIconField.setLeft(new FontIcon(Feather.MAP_PIN));
        rightIconField.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Right", rightIconField);
    }

    private SampleBlock bothIconsSample() {
        var bothIconField = new CustomTextField("Text");
        bothIconField.setLeft(new FontIcon(Feather.MAP_PIN));
        bothIconField.setRight(new FontIcon(Feather.X));
        bothIconField.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Both Sides", bothIconField);
    }

    private SampleBlock successSample() {
        var successField = new CustomTextField("Text");
        successField.pseudoClassStateChanged(STATE_SUCCESS, true);
        successField.setRight(new FontIcon(Feather.X));
        successField.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Success", successField);
    }

    private SampleBlock dangerSample() {
        var dangerField = new CustomTextField("Text");
        dangerField.pseudoClassStateChanged(STATE_DANGER, true);
        dangerField.setLeft(new FontIcon(Feather.MAP_PIN));
        dangerField.setPrefWidth(PREF_WIDTH);
        return new SampleBlock("Danger", dangerField);
    }
}
