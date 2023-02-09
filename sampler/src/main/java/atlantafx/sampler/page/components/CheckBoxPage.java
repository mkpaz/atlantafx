/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CheckBoxPage extends AbstractPage {

    public static final String NAME = "CheckBox";

    @Override
    public String getName() {
        return NAME;
    }

    private CheckBox basicCheck;
    private CheckBox indeterminateCheck;

    public CheckBoxPage() {
        super();
        createView();
    }

    private void createView() {
        setUserContent(new FlowPane(
            PAGE_HGAP, PAGE_VGAP,
            basicSample(),
            indeterminateSample(),
            disabledSample()
        ));
    }

    private SampleBlock basicSample() {
        basicCheck = new CheckBox("_Check Me");
        basicCheck.setMnemonicParsing(true);
        return new SampleBlock("Basic", basicCheck);
    }

    private SampleBlock indeterminateSample() {
        indeterminateCheck = new CheckBox("C_heck Me");
        indeterminateCheck.setAllowIndeterminate(true);
        indeterminateCheck.setIndeterminate(true);
        indeterminateCheck.setMnemonicParsing(true);
        return new SampleBlock("Indeterminate", indeterminateCheck);
    }

    private SampleBlock disabledSample() {
        var basicCheck = new CheckBox("Check Me");
        basicCheck.setSelected(true);
        basicCheck.setDisable(true);

        var indeterminateCheck = new CheckBox("Check Me");
        indeterminateCheck.setAllowIndeterminate(true);
        indeterminateCheck.setIndeterminate(true);
        indeterminateCheck.setDisable(true);

        return new SampleBlock(
            "Disabled",
            new HBox(SampleBlock.BLOCK_HGAP, basicCheck, indeterminateCheck)
        );
    }

    // visually compare normal and indeterminate checkboxes size
    @Override
    protected void onRendered() {
        var normalBox = basicCheck.lookup(".box");
        var indeterminateBox = indeterminateCheck.lookup(".box");

        if (normalBox == null || indeterminateBox == null) {
            return;
        }

        // force layout to obtain node bounds
        ((StackPane) normalBox).layout();
        ((StackPane) indeterminateBox).layout();

        System.out.printf("Basic: height = %.2f , width = %.2f\n",
            normalBox.getBoundsInParent().getHeight(),
            normalBox.getBoundsInParent().getWidth()
        );

        System.out.printf("Indeterminate: height = %.2f , width = %.2f\n",
            indeterminateBox.getBoundsInParent().getHeight(),
            indeterminateBox.getBoundsInParent().getWidth()
        );
    }
}
