/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.InlineDatePicker;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.Popover.ArrowLocation;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;

import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

public class PopoverPage extends AbstractPage {

    public static final String NAME = "Popover";

    @Override
    public String getName() { return NAME; }

    public PopoverPage() {
        super();
        setUserContent(new VBox(Page.PAGE_VGAP,
                new HBox(PAGE_HGAP, textSample(), datePickerSample()),
                positionSample()
        ));
    }

    private SampleBlock textSample() {
        var popover = new Popover(createTextFlow(30));
        popover.setTitle("Lorem Ipsum");
        popover.setHeaderAlwaysVisible(true);
        popover.setDetachable(true);

        var link = createHyperlink("Click me");
        link.setOnAction(e -> popover.show(link));

        return new SampleBlock("Text", link);
    }

    private SampleBlock datePickerSample() {
        var datePicker = new InlineDatePicker();
        datePicker.setValue(LocalDate.now());

        var popover = new Popover(datePicker);
        popover.setHeaderAlwaysVisible(false);
        popover.setDetachable(true);

        var link = createHyperlink("Click me");
        link.setOnAction(e -> popover.show(link));
        new CSSFragment("""
                .popover .date-picker-popup {
                  -color-date-border: transparent;
                  -color-date-bg: transparent;
                  -color-date-day-bg: transparent;
                  -color-date-month-year-bg: transparent;
                  -color-date-day-bg-hover: -color-bg-subtle;
                }
                """
        ).addTo(link);

        return new SampleBlock("Date Picker", link);
    }

    private SampleBlock positionSample() {
        var grid = new GridPane();
        grid.setHgap(BLOCK_HGAP);
        grid.setVgap(BLOCK_VGAP);

        grid.add(createArrowPositionBlock(ArrowLocation.TOP_LEFT), 0, 0);
        grid.add(createArrowPositionBlock(ArrowLocation.TOP_CENTER), 0, 1);
        grid.add(createArrowPositionBlock(ArrowLocation.TOP_RIGHT), 0, 2);

        grid.add(createArrowPositionBlock(ArrowLocation.RIGHT_TOP), 1, 0);
        grid.add(createArrowPositionBlock(ArrowLocation.RIGHT_CENTER), 1, 1);
        grid.add(createArrowPositionBlock(ArrowLocation.RIGHT_BOTTOM), 1, 2);

        grid.add(createArrowPositionBlock(ArrowLocation.BOTTOM_LEFT), 2, 0);
        grid.add(createArrowPositionBlock(ArrowLocation.BOTTOM_CENTER), 2, 1);
        grid.add(createArrowPositionBlock(ArrowLocation.BOTTOM_RIGHT), 2, 2);

        grid.add(createArrowPositionBlock(ArrowLocation.LEFT_TOP), 3, 0);
        grid.add(createArrowPositionBlock(ArrowLocation.LEFT_CENTER), 3, 1);
        grid.add(createArrowPositionBlock(ArrowLocation.LEFT_BOTTOM), 3, 2);

        return new SampleBlock("Position", grid);
    }

    private Hyperlink createHyperlink(String text) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setMinWidth(50);
        hyperlink.setMinHeight(50);
        hyperlink.setAlignment(Pos.CENTER_LEFT);
        return hyperlink;
    }

    private TextFlow createTextFlow(int wordCount) {
        var textFlow = new TextFlow(new Text(FAKER.lorem().sentence(wordCount)));
        textFlow.setPrefWidth(300);
        return textFlow;
    }

    private Hyperlink createArrowPositionBlock(ArrowLocation arrowLocation) {
        var popover = new Popover(createTextFlow(50));
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(arrowLocation);

        var link = createHyperlink(String.valueOf(arrowLocation));
        link.setOnAction(e -> popover.show(link));

        return link;
    }
}
