/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Timeline;
import atlantafx.base.controls.TimelineItem;
import atlantafx.base.controls.TimelineItem.Status;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class TimelinePage extends OutlinePage {

    public static final String NAME = "Timeline";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public TimelinePage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Timeline[/i] control displays a vertical list of events in \
            chronological order. Each item can have a timestamp, content, and \
            a status indicator (pending, active, completed)."""
        );
        addSection("Basic", basicExample());
        addSection("Statuses", statusExample());
        addSection("Alternate", alternateExample());
    }

    private Node basicExample() {
        //snippet_1:start
        var timeline = new Timeline();
        timeline.getItems().addAll(
            new TimelineItem("09:00", "Morning standup meeting"),
            new TimelineItem("10:30", "Code review session"),
            new TimelineItem("14:00", "Feature development"),
            new TimelineItem("16:00", "Deploy to staging")
        );
        //snippet_1:end

        var box = new VBox(10, timeline);
        var description = BBCodeParser.createFormattedText("""
            A basic [i]Timeline[/i] displays items with timestamps on the left \
            and content on the right, connected by a vertical line."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node statusExample() {
        //snippet_2:start
        var timeline = new Timeline();
        timeline.getItems().addAll(
            new TimelineItem("Step 1", "Project setup", Status.COMPLETED),
            new TimelineItem("Step 2", "Implement core features", Status.COMPLETED),
            new TimelineItem("Step 3", "Write tests", Status.ACTIVE),
            new TimelineItem("Step 4", "Deploy to production", Status.PENDING)
        );
        //snippet_2:end

        var box = new VBox(10, timeline);
        var description = BBCodeParser.createFormattedText("""
            Each item can have a status: [code]COMPLETED[/code], [code]ACTIVE[/code], \
            or [code]PENDING[/code]. The node indicator changes color based on status."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node alternateExample() {
        //snippet_3:start
        var timeline = new Timeline();
        timeline.setAlignment(Timeline.Alignment.ALTERNATE);
        timeline.getItems().addAll(
            new TimelineItem("Jan", "Project kickoff"),
            new TimelineItem("Feb", "Design phase completed"),
            new TimelineItem("Mar", "Development sprint 1"),
            new TimelineItem("Apr", "Beta release")
        );
        //snippet_3:end

        var box = new VBox(10, timeline);
        var description = BBCodeParser.createFormattedText("""
            Set [code]alignment[/code] to [code]ALTERNATE[/code] to display \
            items alternating left and right of the timeline."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }
}
