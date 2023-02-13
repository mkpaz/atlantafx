/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class RadioButtonPage extends AbstractPage {

    public static final String NAME = "RadioButton";

    @Override
    public String getName() {
        return NAME;
    }

    public RadioButtonPage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_HGAP, Page.PAGE_VGAP,
            basicSample(),
            groupSample(),
            disabledSample()
        ));
    }

    private SampleBlock basicSample() {
        var radio1 = new RadioButton("_Check Me");
        radio1.setMnemonicParsing(true);

        var radio2 = new RadioButton("Check Me");

        return new SampleBlock("Basic", new VBox(BLOCK_VGAP, radio1, radio2));
    }

    private SampleBlock disabledSample() {
        var radio = new RadioButton("Check Me");
        radio.setDisable(true);
        return new SampleBlock("Disabled", radio);
    }

    private SampleBlock groupSample() {
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
