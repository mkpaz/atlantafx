/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CheckBoxPage extends AbstractPage {

    public static final String NAME = "CheckBox";

    @Override
    public String getName() { return NAME; }

    private CheckBox basicCheck;
    private CheckBox indeterminateCheck;

    public CheckBoxPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                basicSamples(),
                disabledSamples()
        );
    }

    private HBox basicSamples() {
        basicCheck = new CheckBox("_Check Me");
        basicCheck.setMnemonicParsing(true);
        basicCheck.setOnAction(PRINT_SOURCE);
        var basicBlock = new SampleBlock("Basic", basicCheck);

        indeterminateCheck = new CheckBox("C_heck Me");
        indeterminateCheck.setAllowIndeterminate(true);
        indeterminateCheck.setIndeterminate(true);
        indeterminateCheck.setMnemonicParsing(true);
        indeterminateCheck.setOnAction(PRINT_SOURCE);
        var indeterminateBlock = new SampleBlock("Indeterminate", indeterminateCheck);

        var root = new HBox(20);
        root.getChildren().addAll(
                basicBlock.getRoot(),
                indeterminateBlock.getRoot()
        );

        return root;
    }

    private HBox disabledSamples() {
        var basicCheck = new CheckBox("Check Me");
        basicCheck.setSelected(true);
        basicCheck.setDisable(true);

        var indeterminateCheck = new CheckBox("Check Me");
        indeterminateCheck.setAllowIndeterminate(true);
        indeterminateCheck.setIndeterminate(true);
        indeterminateCheck.setDisable(true);

        var disabledBlock = new SampleBlock("Disabled", new HBox(10, basicCheck, indeterminateCheck));

        var root = new HBox(20);
        root.getChildren().addAll(disabledBlock.getRoot());

        return root;
    }

    // visually compare normal and indeterminate checkboxes size
    protected void onRendered() {
        var normalBox = basicCheck.lookup(".box");
        var indeterminateBox = indeterminateCheck.lookup(".box");

        if (normalBox == null || indeterminateBox == null) { return; }

        // force layout to obtain node bounds
        ((StackPane) normalBox).layout();
        ((StackPane) indeterminateBox).layout();

        basicCheck.setText(String.format("_Check Me (size = H%.2f x W%.2f)",
                normalBox.getBoundsInParent().getHeight(),
                normalBox.getBoundsInParent().getWidth()
        ));
        indeterminateCheck.setText(String.format("C_heck Me (box size = H%.2f x W%.2f)",
                normalBox.getBoundsInParent().getHeight(),
                normalBox.getBoundsInParent().getWidth()
        ));
    }
}
