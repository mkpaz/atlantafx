/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class NotificationTest {

    private Notification notification;

    @BeforeEach
    public void setUp() {
        notification = new Notification();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(notification.getStyleClass()).contains("notification");
    }

    @Test
    public void testDefaultPropertyValues() {
        assertThat(notification.getMessage()).isNull();
        assertThat(notification.getGraphic()).isNull();
        assertThat(notification.getOnClose()).isNull();
        assertThat(notification.getPrimaryActions()).isNotNull();
        assertThat(notification.getPrimaryActions()).isEmpty();
        assertThat(notification.getSecondaryActions()).isNotNull();
        assertThat(notification.getSecondaryActions()).isEmpty();
    }

    @Test
    public void testDefaultSizePreferences() {
        assertThat(notification.getPrefWidth()).isEqualTo(400);
        assertThat(notification.getMaxWidth()).isEqualTo(Region.USE_PREF_SIZE);
    }

    @Test
    public void testEmptyConstructor() {
        var n = new Notification();
        assertThat(n.getMessage()).isNull();
        assertThat(n.getGraphic()).isNull();
    }

    @Test
    public void testMessageConstructor() {
        var n = new Notification("Hello");
        assertThat(n.getMessage()).isEqualTo("Hello");
        assertThat(n.getGraphic()).isNull();
    }

    @Test
    public void testMessageAndGraphicConstructor() {
        var graphic = new Region();
        var n = new Notification("Hello", graphic);
        assertThat(n.getMessage()).isEqualTo("Hello");
        assertThat(n.getGraphic()).isSameAs(graphic);
    }

    @Test
    public void testMessageProperty() {
        notification.setMessage("Test message");
        assertThat(notification.getMessage()).isEqualTo("Test message");
        assertThat(notification.messageProperty().get()).isEqualTo("Test message");

        notification.setMessage(null);
        assertThat(notification.getMessage()).isNull();
    }

    @Test
    public void testGraphicProperty() {
        var graphic = new Region();
        notification.setGraphic(graphic);
        assertThat(notification.getGraphic()).isSameAs(graphic);
        assertThat(notification.graphicProperty().get()).isSameAs(graphic);

        notification.setGraphic(null);
        assertThat(notification.getGraphic()).isNull();
    }

    @Test
    public void testOnCloseProperty() {
        assertThat(notification.getOnClose()).isNull();

        var handler = new javafx.event.EventHandler<javafx.event.Event>() {
            @Override
            public void handle(javafx.event.Event event) {}
        };
        notification.setOnClose(handler);
        assertThat(notification.getOnClose()).isSameAs(handler);
        assertThat(notification.onCloseProperty().get()).isSameAs(handler);

        notification.setOnClose(null);
        assertThat(notification.getOnClose()).isNull();
    }

    @Test
    public void testPrimaryActionsProperty() {
        var btn1 = new Button("OK");
        var btn2 = new Button("Cancel");
        notification.setPrimaryActions(btn1, btn2);

        assertThat(notification.getPrimaryActions()).hasSize(2);
        assertThat(notification.getPrimaryActions()).containsExactly(btn1, btn2);
    }

    @Test
    public void testPrimaryActionsReplace() {
        var btn1 = new Button("OK");
        notification.setPrimaryActions(btn1);
        assertThat(notification.getPrimaryActions()).hasSize(1);

        var btn2 = new Button("Retry");
        notification.setPrimaryActions(btn2);
        assertThat(notification.getPrimaryActions()).hasSize(1);
        assertThat(notification.getPrimaryActions()).containsExactly(btn2);
    }

    @Test
    public void testSetPrimaryActionsList() {
        var buttons = FXCollections.observableArrayList(new Button("A"), new Button("B"));
        notification.setPrimaryActions(buttons);
        assertThat(notification.getPrimaryActions()).isSameAs(buttons);
    }

    @Test
    public void testSecondaryActionsProperty() {
        var item1 = new MenuItem("Settings");
        var item2 = new MenuItem("Help");
        notification.setSecondaryActions(item1, item2);

        assertThat(notification.getSecondaryActions()).hasSize(2);
        assertThat(notification.getSecondaryActions()).containsExactly(item1, item2);
    }

    @Test
    public void testSecondaryActionsReplace() {
        var item1 = new MenuItem("Settings");
        notification.setSecondaryActions(item1);
        assertThat(notification.getSecondaryActions()).hasSize(1);

        var item2 = new MenuItem("About");
        notification.setSecondaryActions(item2);
        assertThat(notification.getSecondaryActions()).hasSize(1);
        assertThat(notification.getSecondaryActions()).containsExactly(item2);
    }

    @Test
    public void testSetSecondaryActionsList() {
        var items = FXCollections.observableArrayList(new MenuItem("A"), new MenuItem("B"));
        notification.setSecondaryActions(items);
        assertThat(notification.getSecondaryActions()).isSameAs(items);
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = notification.createDefaultSkin();
        assertThat(skin).isInstanceOf(NotificationSkin.class);
    }
}
