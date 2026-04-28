/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class PopoverTest {

    private Popover popover;

    @BeforeEach
    public void setUp() {
        popover = new Popover();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(popover.getStyleClass()).contains("popover");
    }

    @Test
    public void testDefaultContentNode() {
        assertThat(popover.getContentNode()).isNotNull();
    }

    @Test
    public void testContentNodeProperty() {
        var label = new Label("Test content");
        popover.setContentNode(label);
        assertThat(popover.getContentNode()).isSameAs(label);
        assertThat(popover.contentNodeProperty().get()).isSameAs(label);
    }

    @Test
    public void testConstructorWithContent() {
        var label = new Label("Custom");
        var p = new Popover(label);
        assertThat(p.getContentNode()).isSameAs(label);
    }

    @Test
    public void testTitleProperty() {
        assertThat(popover.getTitle()).isEqualTo("Info");

        popover.setTitle("Custom Title");
        assertThat(popover.getTitle()).isEqualTo("Custom Title");
        assertThat(popover.titleProperty().get()).isEqualTo("Custom Title");
    }

    @Test
    public void testArrowLocationProperty() {
        assertThat(popover.getArrowLocation()).isEqualTo(Popover.ArrowLocation.LEFT_TOP);

        popover.setArrowLocation(Popover.ArrowLocation.BOTTOM_CENTER);
        assertThat(popover.getArrowLocation()).isEqualTo(Popover.ArrowLocation.BOTTOM_CENTER);
        assertThat(popover.arrowLocationProperty().get()).isEqualTo(Popover.ArrowLocation.BOTTOM_CENTER);
    }

    @Test
    public void testArrowSizeProperty() {
        assertThat(popover.getArrowSize()).isEqualTo(12.0);

        popover.setArrowSize(20.0);
        assertThat(popover.getArrowSize()).isEqualTo(20.0);
        assertThat(popover.arrowSizeProperty().get()).isEqualTo(20.0);
    }

    @Test
    public void testArrowIndentProperty() {
        assertThat(popover.getArrowIndent()).isEqualTo(12.0);

        popover.setArrowIndent(8.0);
        assertThat(popover.getArrowIndent()).isEqualTo(8.0);
        assertThat(popover.arrowIndentProperty().get()).isEqualTo(8.0);
    }

    @Test
    public void testCornerRadiusProperty() {
        assertThat(popover.getCornerRadius()).isEqualTo(6.0);

        popover.setCornerRadius(10.0);
        assertThat(popover.getCornerRadius()).isEqualTo(10.0);
        assertThat(popover.cornerRadiusProperty().get()).isEqualTo(10.0);
    }

    @Test
    public void testDetachableProperty() {
        assertThat(popover.isDetachable()).isTrue();

        popover.setDetachable(false);
        assertThat(popover.isDetachable()).isFalse();
    }

    @Test
    public void testDetachedProperty() {
        assertThat(popover.isDetached()).isFalse();

        popover.setDetached(true);
        assertThat(popover.isDetached()).isTrue();
    }

    @Test
    public void testHeaderAlwaysVisibleProperty() {
        assertThat(popover.isHeaderAlwaysVisible()).isFalse();

        popover.setHeaderAlwaysVisible(true);
        assertThat(popover.isHeaderAlwaysVisible()).isTrue();
    }

    @Test
    public void testCloseButtonEnabledProperty() {
        assertThat(popover.isCloseButtonEnabled()).isTrue();

        popover.setCloseButtonEnabled(false);
        assertThat(popover.isCloseButtonEnabled()).isFalse();
    }

    @Test
    public void testAnimatedProperty() {
        assertThat(popover.isAnimated()).isTrue();

        popover.setAnimated(false);
        assertThat(popover.isAnimated()).isFalse();
    }

    @Test
    public void testFadeInDurationProperty() {
        assertThat(popover.getFadeInDuration()).isEqualTo(Duration.seconds(0.2));

        var custom = Duration.millis(500);
        popover.setFadeInDuration(custom);
        assertThat(popover.getFadeInDuration()).isEqualTo(custom);
    }

    @Test
    public void testFadeOutDurationProperty() {
        assertThat(popover.getFadeOutDuration()).isEqualTo(Duration.seconds(0.2));

        var custom = Duration.millis(300);
        popover.setFadeOutDuration(custom);
        assertThat(popover.getFadeOutDuration()).isEqualTo(custom);
    }

    @Test
    public void testAutoHideIsTrueByDefault() {
        assertThat(popover.isAutoHide()).isTrue();
    }

    @Test
    public void testDetachWhenDetachable() {
        popover.setDetachable(true);
        popover.detach();
        assertThat(popover.isDetached()).isTrue();
    }

    @Test
    public void testDetachWhenNotDetachable() {
        popover.setDetachable(false);
        popover.detach();
        assertThat(popover.isDetached()).isFalse();
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = popover.createDefaultSkin();
        assertThat(skin).isInstanceOf(PopoverSkin.class);
    }

    @Test
    public void testArrowLocationValues() {
        for (var location : Popover.ArrowLocation.values()) {
            popover.setArrowLocation(location);
            assertThat(popover.getArrowLocation()).isEqualTo(location);
        }
    }
}
