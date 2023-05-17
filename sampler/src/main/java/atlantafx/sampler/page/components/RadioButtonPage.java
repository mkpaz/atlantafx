/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public final class RadioButtonPage extends OutlinePage {

    public static final String NAME = "RadioButton";

    @Override
    public String getName() {
        return NAME;
    }

    public RadioButtonPage() {
        super();

        addPageHeader();
        addFormattedText("""
            [i]RadioButton[/i]'s create a series of items where only one item can be selected."""
        );
        addSection("Usage", usageExample());
        addSection("Toggle Group", toggleGroupExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var radio1 = new RadioButton("_Check Me");
        radio1.setMnemonicParsing(true);

        var radio2 = new RadioButton("Check Me");
        //snippet_1:end

        var box = new VBox(VGAP_10, radio1, radio2);
        var description = BBCodeParser.createFormattedText("""
            A radio button control can be either selected or deselected.""");

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox toggleGroupExample() {
        //snippet_2:start
        var group = new ToggleGroup();

        var musicRadio = new RadioButton("Music");
        musicRadio.setToggleGroup(group);
        musicRadio.setSelected(true);

        var imagesRadio = new RadioButton("Images");
        imagesRadio.setToggleGroup(group);

        var videosRadio = new RadioButton("Videos");
        videosRadio.setToggleGroup(group);
        //snippet_2:end

        var box = new VBox(VGAP_10, musicRadio, imagesRadio, videosRadio);
        var description = BBCodeParser.createFormattedText("""
            Typically radio buttons are combined into a group where only one button \
            at a time can be selected. This behavior distinguishes them from toggle buttons, \
            because all toggle buttons in a group can be in a deselected state.""");

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }
}
