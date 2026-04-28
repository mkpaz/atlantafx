/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import atlantafx.base.controls.TimelineItem.Status;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class TimelineTest {

    @Test
    public void testDefaultStyleClass() {
        var timeline = new Timeline();
        assertThat(timeline.getStyleClass()).contains("timeline");
    }

    @Test
    public void testDefaultProperties() {
        var timeline = new Timeline();
        assertThat(timeline.getAlignment()).isEqualTo(Timeline.Alignment.LEFT);
        assertThat(timeline.getItems()).isEmpty();
    }

    @Test
    public void testAlignmentProperty() {
        var timeline = new Timeline();
        timeline.setAlignment(Timeline.Alignment.ALTERNATE);
        assertThat(timeline.getAlignment()).isEqualTo(Timeline.Alignment.ALTERNATE);
    }

    @Test
    public void testItemsList() {
        var timeline = new Timeline();
        timeline.getItems().addAll(
            new TimelineItem("a", "b"),
            new TimelineItem("c", "d")
        );
        assertThat(timeline.getItems()).hasSize(2);
    }

    @Test
    public void testTimelineItemProperties() {
        var item = new TimelineItem("09:00", "Meeting", Status.ACTIVE);
        assertThat(item.getTimestamp()).isEqualTo("09:00");
        assertThat(item.getContent()).isEqualTo("Meeting");
        assertThat(item.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(item.getGraphic()).isNull();
    }

    @Test
    public void testTimelineItemStatusChange() {
        var item = new TimelineItem();
        item.setStatus(Status.COMPLETED);
        assertThat(item.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    public void testCreateDefaultSkin() {
        var timeline = new Timeline();
        var skin = timeline.createDefaultSkin();
        assertThat(skin).isInstanceOf(TimelineSkin.class);
    }
}
