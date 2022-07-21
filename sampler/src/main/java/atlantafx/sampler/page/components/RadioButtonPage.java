/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RadioButtonPage extends AbstractPage {

    public static final String NAME = "RadioButton";

    @Override
    public String getName() { return NAME; }

    public RadioButtonPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                basicSamples(),
                groupSamples().getRoot()
        );
    }

    private HBox basicSamples() {
        var basicRadio = new RadioButton("_Check Me");
        basicRadio.setMnemonicParsing(true);
        basicRadio.setOnAction(PRINT_SOURCE);
        var basicBlock = new SampleBlock("Basic", basicRadio);

        var disabledRadio = new RadioButton("Check Me");
        disabledRadio.setDisable(true);
        var disabledBlock = new SampleBlock("Disabled", disabledRadio);

        var root = new HBox(20);
        root.getChildren().addAll(
                basicBlock.getRoot(),
                disabledBlock.getRoot()
        );

        return root;
    }

    private SampleBlock groupSamples() {
        var group = new ToggleGroup();

        var musicRadio = new RadioButton("Music");
        musicRadio.setToggleGroup(group);
        musicRadio.setSelected(true);

        var imagesRadio = new RadioButton("Images");
        imagesRadio.setToggleGroup(group);

        var videosRadio = new RadioButton("Videos");
        videosRadio.setToggleGroup(group);

        return new SampleBlock("Toggle Group", new VBox(5, musicRadio, imagesRadio, videosRadio));
    }
}
