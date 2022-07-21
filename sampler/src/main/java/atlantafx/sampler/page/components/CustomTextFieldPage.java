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

    @Override
    public String getName() { return NAME; }

    public CustomTextFieldPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(samples());
    }

    private FlowPane samples() {
        var leftIconField = new CustomTextField();
        leftIconField.setPromptText("Prompt text");
        leftIconField.setRight(new FontIcon(Feather.X));
        var leftIconBlock = new SampleBlock("Node on the left", leftIconField);

        var rightIconField = new CustomTextField();
        rightIconField.setPromptText("Prompt text");
        rightIconField.setLeft(new FontIcon(Feather.MAP_PIN));
        var rightIconBlock = new SampleBlock("Node on the right", rightIconField);

        var bothIconField = new CustomTextField("Text");
        bothIconField.setLeft(new FontIcon(Feather.MAP_PIN));
        bothIconField.setRight(new FontIcon(Feather.X));
        var bothIconBlock = new SampleBlock("Nodes on both sides", bothIconField);

        var noSideIconsField = new CustomTextField("Text");
        var noSideIconsBlock = new SampleBlock("No side nodes", noSideIconsField);

        var successField = new CustomTextField("Text");
        successField.pseudoClassStateChanged(STATE_SUCCESS, true);
        successField.setRight(new FontIcon(Feather.X));
        var successBlock = new SampleBlock("Success", successField);

        var dangerField = new CustomTextField("Text");
        dangerField.pseudoClassStateChanged(STATE_DANGER, true);
        dangerField.setLeft(new FontIcon(Feather.MAP_PIN));
        var dangerBlock = new SampleBlock("Danger", dangerField);

        var flowPane = new FlowPane(20, 20);
        flowPane.getChildren().setAll(
                leftIconBlock.getRoot(),
                rightIconBlock.getRoot(),
                bothIconBlock.getRoot(),
                noSideIconsBlock.getRoot(),
                successBlock.getRoot(),
                dangerBlock.getRoot()
        );

        return flowPane;
    }
}
