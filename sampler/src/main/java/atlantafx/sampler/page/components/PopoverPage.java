/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.InlineDatePicker;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.Popover.ArrowLocation;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;

public class PopoverPage extends AbstractPage {

    public static final String NAME = "Popover";

    @Override
    public String getName() { return NAME; }

    public PopoverPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().setAll(basicSamples(), positionSamples().getRoot());
    }

    private HBox basicSamples() {
        var textPopover = new Popover(textFlow(30));
        textPopover.setTitle("Lorem Ipsum");
        textPopover.setHeaderAlwaysVisible(true);
        textPopover.setDetachable(true);
        var textLink = hyperlink("Click me");
        textLink.setOnAction(e -> textPopover.show(textLink));
        var textBlock = new SampleBlock("Basic", textLink);

        var datePicker = new InlineDatePicker();
        datePicker.setValue(LocalDate.now());

        var datePopover = new Popover(datePicker);
        textPopover.setHeaderAlwaysVisible(false);
        datePopover.setDetachable(true);
        var dateLink = hyperlink("Click me");
        dateLink.setOnAction(e -> datePopover.show(dateLink));
        var dateBlock = new SampleBlock("Date picker", dateLink);

        var box = new HBox(20,
                           textBlock.getRoot(),
                           dateBlock.getRoot()
        );
        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }

    private SampleBlock positionSamples() {
        var grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        grid.add(arrowPositionBlock(ArrowLocation.TOP_LEFT), 0, 0);
        grid.add(arrowPositionBlock(ArrowLocation.TOP_CENTER), 0, 1);
        grid.add(arrowPositionBlock(ArrowLocation.TOP_RIGHT), 0, 2);

        grid.add(arrowPositionBlock(ArrowLocation.RIGHT_TOP), 1, 0);
        grid.add(arrowPositionBlock(ArrowLocation.RIGHT_CENTER), 1, 1);
        grid.add(arrowPositionBlock(ArrowLocation.RIGHT_BOTTOM), 1, 2);

        grid.add(arrowPositionBlock(ArrowLocation.BOTTOM_LEFT), 2, 0);
        grid.add(arrowPositionBlock(ArrowLocation.BOTTOM_CENTER), 2, 1);
        grid.add(arrowPositionBlock(ArrowLocation.BOTTOM_RIGHT), 2, 2);

        grid.add(arrowPositionBlock(ArrowLocation.LEFT_TOP), 3, 0);
        grid.add(arrowPositionBlock(ArrowLocation.LEFT_CENTER), 3, 1);
        grid.add(arrowPositionBlock(ArrowLocation.LEFT_BOTTOM), 3, 2);

        return new SampleBlock("Position", grid);
    }

    private Hyperlink hyperlink(String text) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setMinWidth(50);
        hyperlink.setMinHeight(50);
        hyperlink.setAlignment(Pos.CENTER_LEFT);
        return hyperlink;
    }

    private TextFlow textFlow(int wordCount) {
        var textFlow = new TextFlow(new Text(FAKER.lorem().sentence(wordCount)));
        textFlow.setPrefWidth(300);
        return textFlow;
    }

    private Hyperlink arrowPositionBlock(ArrowLocation arrowLocation) {
        var link = hyperlink(String.valueOf(arrowLocation));
        var popover = new Popover(textFlow(50));
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(arrowLocation);
        link.setOnAction(e -> popover.show(link));
        return link;
    }
}
