/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.jspecify.annotations.Nullable;

/**
 * Represents a single item in a {@link Timeline} control.
 */
public class TimelineItem {

    public enum Status { PENDING, ACTIVE, COMPLETED }

    private static final PseudoClass PENDING =
            PseudoClass.getPseudoClass("pending");
    private static final PseudoClass ACTIVE =
            PseudoClass.getPseudoClass("active");
    private static final PseudoClass COMPLETED =
            PseudoClass.getPseudoClass("completed");

    private final StringProperty timestamp = new SimpleStringProperty(this, "timestamp");
    private final StringProperty content = new SimpleStringProperty(this, "content");
    private final ObjectProperty<@Nullable Node> graphic =
            new SimpleObjectProperty<>(this, "graphic");
    private final ObjectProperty<Status> status =
            new SimpleObjectProperty<>(this, "status", Status.PENDING);

    public TimelineItem() {}

    public TimelineItem(String timestamp, String content) {
        setTimestamp(timestamp);
        setContent(content);
    }

    public TimelineItem(String timestamp, String content, Status status) {
        setTimestamp(timestamp);
        setContent(content);
        setStatus(status);
    }

    public StringProperty timestampProperty() { return timestamp; }
    public String getTimestamp() { return timestamp.get(); }
    public void setTimestamp(String value) { timestamp.set(value); }

    public StringProperty contentProperty() { return content; }
    public String getContent() { return content.get(); }
    public void setContent(String value) { content.set(value); }

    public ObjectProperty<@Nullable Node> graphicProperty() { return graphic; }
    public @Nullable Node getGraphic() { return graphic.get(); }
    public void setGraphic(@Nullable Node value) { graphic.set(value); }

    public ObjectProperty<Status> statusProperty() { return status; }
    public Status getStatus() { return status.get(); }
    public void setStatus(Status value) { status.set(value); }

    void activatePseudoClasses(Node node) {
        Status s = getStatus();
        node.pseudoClassStateChanged(PENDING, s == Status.PENDING);
        node.pseudoClassStateChanged(ACTIVE, s == Status.ACTIVE);
        node.pseudoClassStateChanged(COMPLETED, s == Status.COMPLETED);
    }
}
