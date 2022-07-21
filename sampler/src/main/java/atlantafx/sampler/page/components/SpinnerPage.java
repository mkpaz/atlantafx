/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.util.IntegerStringConverter;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;

public final class SpinnerPage extends AbstractPage {

    public static final String NAME = "Spinner";
    private static final int PREF_WIDTH = 120;

    @Override
    public String getName() { return NAME; }

    public SpinnerPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                basicSamples(),
                arrowPositionSamples()
        );
    }

    private FlowPane basicSamples() {
        var editableSpin = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(editableSpin);
        editableSpin.setPrefWidth(PREF_WIDTH);
        editableSpin.setEditable(true);
        var editableBlock = new SampleBlock("Editable", editableSpin);

        var disabledSpin = new Spinner<Integer>(1, 10, 1);
        disabledSpin.setPrefWidth(PREF_WIDTH);
        disabledSpin.setDisable(true);
        var disabledBlock = new SampleBlock("Disabled", disabledSpin);

        var root = new FlowPane(20, 20);
        root.getChildren().addAll(
                editableBlock.getRoot(),
                disabledBlock.getRoot()
        );

        return root;
    }

    private FlowPane arrowPositionSamples() {
        var leftVSpin = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(leftVSpin);
        leftVSpin.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        leftVSpin.setPrefWidth(PREF_WIDTH);
        leftVSpin.setEditable(true);
        var leftVBlock = new SampleBlock("Arrows on left & vertical", leftVSpin);

        var leftHSpin = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(leftHSpin);
        leftHSpin.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL);
        leftHSpin.setPrefWidth(PREF_WIDTH);
        leftHSpin.setEditable(true);
        var leftHBlock = new SampleBlock("Arrows on left & horizontal", leftHSpin);

        var rightHSpin = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(rightHSpin);
        rightHSpin.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        rightHSpin.setPrefWidth(PREF_WIDTH);
        rightHSpin.setEditable(true);
        var rightHBlock = new SampleBlock("Arrows on right & horizontal", rightHSpin);

        var splitHSpin = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(splitHSpin);
        splitHSpin.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        splitHSpin.setPrefWidth(PREF_WIDTH);
        splitHSpin.setEditable(true);
        var splitHBlock = new SampleBlock("Split arrows & horizontal", splitHSpin);

        var splitVSpin = new Spinner<Integer>(1, 10, 1);
        IntegerStringConverter.createFor(splitVSpin);
        splitVSpin.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
        splitVSpin.setEditable(true);
        splitVSpin.setPrefWidth(40);
        var splitVBlock = new SampleBlock("Split arrows & vertical", splitVSpin);

        var root = new FlowPane(20, 20);
        root.getChildren().addAll(
                leftVBlock.getRoot(),
                leftHBlock.getRoot(),
                rightHBlock.getRoot(),
                splitHBlock.getRoot(),
                splitVBlock.getRoot()
        );

        return root;
    }
}
