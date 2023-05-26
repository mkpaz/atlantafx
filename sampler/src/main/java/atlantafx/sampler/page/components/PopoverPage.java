/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Calendar;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.Popover.ArrowLocation;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class PopoverPage extends OutlinePage {

    public static final String NAME = "Popover";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public PopoverPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Popover[/i] is a control used to display additional information \
            or perform actions. It appears as a small popup window that overlays the \
            main interface, triggered by a user action such as a mouseover or tap. \
            It provides contextual information or options related to a specific object \
            or feature on the interface."""
        );
        addSection("Usage", usageExample());
        addSection("Position", positionExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var textFlow = new TextFlow(new Text(
            FAKER.lorem().sentence(30)
        ));
        textFlow.setPrefWidth(300);
        textFlow.setPadding(new Insets(10, 0, 10, 0));

        var pop1 = new Popover(textFlow);
        pop1.setTitle("Lorem Ipsum");
        pop1.setHeaderAlwaysVisible(true);
        pop1.setDetachable(true);

        var link1 = new Hyperlink("Text");
        link1.setOnAction(e -> pop1.show(link1));

        // ~
        var cal = new Calendar();
        cal.setValue(LocalDate.now(ZoneId.systemDefault()));
        cal.getStyleClass().add(Tweaks.EDGE_TO_EDGE);

        var pop2 = new Popover(cal);
        pop2.setHeaderAlwaysVisible(false);
        pop2.setDetachable(true);

        var link2 = new Hyperlink("Calendar");
        link2.setOnAction(e -> pop2.show(link2));
        //snippet_1:end

        var box = new HBox(HGAP_30, link1, link2);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The popup window has a very lightweight appearance (no default window decorations) \
            and an arrow pointing at the owner. Due to the nature of popup windows the \
            [i]Popover[/i] will move around with the parent window when the user drags it."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox positionExample() {
        //snippet_2:start
        class PopoverLink extends Hyperlink {

            public PopoverLink(ArrowLocation arrowLocation) {
                super();

                var textFlow = new TextFlow(new Text(
                    FAKER.lorem().sentence(30)
                ));
                textFlow.setPrefWidth(300);
                textFlow.setPadding(new Insets(10, 0, 10, 0));

                var pop = new Popover(textFlow);
                pop.setHeaderAlwaysVisible(false);
                pop.setArrowLocation(arrowLocation);

                setText(String.valueOf(arrowLocation));
                setOnAction(e -> pop.show(this));
            }
        }

        var grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.addColumn(0,
            new PopoverLink(ArrowLocation.TOP_LEFT),
            new PopoverLink(ArrowLocation.TOP_CENTER),
            new PopoverLink(ArrowLocation.TOP_RIGHT)
        );
        grid.addColumn(1,
            new PopoverLink(ArrowLocation.RIGHT_TOP),
            new PopoverLink(ArrowLocation.RIGHT_CENTER),
            new PopoverLink(ArrowLocation.RIGHT_BOTTOM)
        );
        grid.addColumn(2,
            new PopoverLink(ArrowLocation.BOTTOM_LEFT),
            new PopoverLink(ArrowLocation.BOTTOM_CENTER),
            new PopoverLink(ArrowLocation.BOTTOM_RIGHT)
        );
        grid.addColumn(3,
            new PopoverLink(ArrowLocation.LEFT_TOP),
            new PopoverLink(ArrowLocation.LEFT_CENTER),
            new PopoverLink(ArrowLocation.LEFT_BOTTOM)
        );
        //snippet_2:end

        var description = BBCodeParser.createFormattedText("""
            The [i]Popover[/i] popup window can be positioned by setting an \
            appropriate ArrowLocation value.."""
        );

        var example = new ExampleBox(grid, new Snippet(getClass(), 2), description);
        example.setAllowDisable(false);

        return example;
    }
}
