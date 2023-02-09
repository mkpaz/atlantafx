/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.STATE_DANGER;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

public class TextAreaPage extends AbstractPage {

    public static final String NAME = "TextArea";
    private static final double CONTROL_WIDTH = 200;
    private static final double CONTROL_HEIGHT = 120;

    @Override
    public String getName() {
        return NAME;
    }

    public TextAreaPage() {
        super();
        setUserContent(new FlowPane(
            PAGE_HGAP, PAGE_VGAP,
            basicSample(),
            promptSample(),
            scrollSample(),
            readonlySample(),
            successSample(),
            dangerSample(),
            disabledSample()
        ));
    }

    private SampleBlock basicSample() {
        var textArea = createTextArea("Text");
        textArea.setWrapText(true);
        return new SampleBlock("Basic", textArea);
    }

    private SampleBlock promptSample() {
        var textArea = createTextArea(null);
        textArea.setPromptText("Prompt text");
        return new SampleBlock("Prompt", textArea);
    }

    private SampleBlock scrollSample() {
        var textArea = createTextArea(
            Stream.generate(() -> FAKER.lorem().paragraph()).limit(10).collect(Collectors.joining("\n"))
        );
        textArea.setWrapText(false);
        return new SampleBlock("Scrolling", textArea);
    }

    private SampleBlock readonlySample() {
        var textArea = createTextArea("Text");
        textArea.setEditable(false);
        return new SampleBlock("Readonly", textArea);
    }

    private SampleBlock successSample() {
        var textArea = createTextArea("Text");
        textArea.pseudoClassStateChanged(STATE_SUCCESS, true);
        return new SampleBlock("Success", textArea);
    }

    private SampleBlock dangerSample() {
        var textArea = createTextArea("Text");
        textArea.pseudoClassStateChanged(STATE_DANGER, true);
        return new SampleBlock("Danger", textArea);
    }

    private SampleBlock disabledSample() {
        var textArea = createTextArea("Text");
        textArea.setDisable(true);
        return new SampleBlock("Disabled", textArea);
    }

    private TextArea createTextArea(String text) {
        var textArea = new TextArea(text);
        textArea.setMinWidth(CONTROL_WIDTH);
        textArea.setMinHeight(CONTROL_HEIGHT);
        textArea.setMaxWidth(CONTROL_WIDTH);
        textArea.setMaxHeight(CONTROL_HEIGHT);
        return textArea;
    }
}
