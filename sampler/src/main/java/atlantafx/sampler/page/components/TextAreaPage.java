/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;

public class TextAreaPage extends AbstractPage {

    public static final String NAME = "TextArea";
    private static final double CONTROL_WIDTH = 200;
    private static final double CONTROL_HEIGHT = 120;

    @Override
    public String getName() { return NAME; }

    public TextAreaPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(samples());
    }

    private FlowPane samples() {
        var basicArea = textArea("Text");
        basicArea.setWrapText(true);
        var basicBlock = new SampleBlock("Basic", basicArea);

        var promptArea = textArea(null);
        promptArea.setPromptText("Prompt text");
        var promptBlock = new SampleBlock("Prompt", promptArea);

        var scrollArea = textArea(
                Stream.generate(() -> FAKER.lorem().paragraph()).limit(10).collect(Collectors.joining("\n"))
        );
        scrollArea.setWrapText(false);
        var scrollBlock = new SampleBlock("Scrolling", scrollArea);

        var readonlyArea = textArea("Text");
        readonlyArea.setEditable(false);
        var readonlyBlock = new SampleBlock("Readonly", readonlyArea);

        var disabledArea = textArea("Text");
        disabledArea.setDisable(true);
        var disabledBlock = new SampleBlock("Disabled", disabledArea);

        var successArea = textArea("Text");
        successArea.pseudoClassStateChanged(STATE_SUCCESS, true);
        var successBlock = new SampleBlock("Success", successArea);

        var dangerArea = textArea("Text");
        dangerArea.pseudoClassStateChanged(STATE_DANGER, true);
        var dangerBlock = new SampleBlock("Danger", dangerArea);

        var flowPane = new FlowPane(20, 20);
        flowPane.getChildren().setAll(
                basicBlock.getRoot(),
                promptBlock.getRoot(),
                scrollBlock.getRoot(),
                readonlyBlock.getRoot(),
                disabledBlock.getRoot(),
                successBlock.getRoot(),
                dangerBlock.getRoot()
        );

        return flowPane;
    }

    private TextArea textArea(String text) {
        var textArea = new TextArea(text);
        textArea.setMinWidth(CONTROL_WIDTH);
        textArea.setMinHeight(CONTROL_HEIGHT);
        textArea.setMaxWidth(CONTROL_WIDTH);
        textArea.setMaxHeight(CONTROL_HEIGHT);
        return textArea;
    }
}
