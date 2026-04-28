/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.collections.FXCollections;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class SegmentedControlTest {

    private SegmentedControl control;

    @BeforeEach
    public void setUp() {
        control = new SegmentedControl();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(control.getStyleClass()).contains("segmented-control");
    }

    @Test
    public void testDefaultFocusTraversableIsFalse() {
        assertThat(control.isFocusTraversable()).isFalse();
    }

    @Test
    public void testDefaultSegmentsListIsEmpty() {
        assertThat(control.getSegments()).isEmpty();
    }

    @Test
    public void testDefaultToggleGroupIsNotNull() {
        assertThat(control.getToggleGroup()).isNotNull();
    }

    @Test
    public void testDefaultAnimationDuration() {
        assertThat(control.getAnimationDuration()).isEqualTo(SegmentedControl.DEFAULT_ANIMATION_DURATION);
    }

    @Test
    public void testConstructorWithStrings() {
        var sc = new SegmentedControl("One", "Two", "Three");
        assertThat(sc.getSegments()).hasSize(3);
        assertThat(sc.getSegments().get(0).getText()).isEqualTo("One");
        assertThat(sc.getSegments().get(1).getText()).isEqualTo("Two");
        assertThat(sc.getSegments().get(2).getText()).isEqualTo("Three");
    }

    @Test
    public void testConstructorWithNullStrings() {
        var sc = new SegmentedControl((String[]) null);
        assertThat(sc.getSegments()).isEmpty();
    }

    @Test
    public void testConstructorWithToggleLabels() {
        var label1 = new ToggleLabel("A");
        var label2 = new ToggleLabel("B");
        var sc = new SegmentedControl(label1, label2);
        assertThat(sc.getSegments()).hasSize(2);
        assertThat(sc.getSegments().get(0)).isSameAs(label1);
        assertThat(sc.getSegments().get(1)).isSameAs(label2);
    }

    @Test
    public void testConstructorWithNullToggleLabels() {
        var sc = new SegmentedControl((ToggleLabel[]) null);
        assertThat(sc.getSegments()).isEmpty();
    }

    @Test
    public void testConstructorWithObservableList() {
        var labels = FXCollections.observableArrayList(
            new ToggleLabel("X"),
            new ToggleLabel("Y")
        );
        var sc = new SegmentedControl(labels);
        assertThat(sc.getSegments()).hasSize(2);
    }

    @Test
    public void testConstructorWithNullObservableList() {
        var sc = new SegmentedControl((javafx.collections.ObservableList<ToggleLabel>) null);
        assertThat(sc.getSegments()).isEmpty();
    }

    @Test
    public void testConstructorWithEmptyObservableList() {
        var sc = new SegmentedControl(FXCollections.observableArrayList());
        assertThat(sc.getSegments()).isEmpty();
    }

    @Test
    public void testAddSegment() {
        var label = new ToggleLabel("New");
        control.getSegments().add(label);
        assertThat(control.getSegments()).hasSize(1);
        assertThat(control.getSegments().get(0)).isSameAs(label);
    }

    @Test
    public void testRemoveSegment() {
        var label1 = new ToggleLabel("A");
        var label2 = new ToggleLabel("B");
        control.getSegments().addAll(label1, label2);
        assertThat(control.getSegments()).hasSize(2);

        control.getSegments().remove(label1);
        assertThat(control.getSegments()).hasSize(1);
        assertThat(control.getSegments().get(0)).isSameAs(label2);
    }

    @Test
    public void testClearSegments() {
        control.getSegments().addAll(new ToggleLabel("A"), new ToggleLabel("B"));
        control.getSegments().clear();
        assertThat(control.getSegments()).isEmpty();
    }

    @Test
    public void testSetToggleGroup() {
        var newGroup = new ToggleGroup();
        assertThat(control.getToggleGroup()).isNotSameAs(newGroup);

        control.setToggleGroup(newGroup);
        assertThat(control.getToggleGroup()).isSameAs(newGroup);
    }

    @Test
    public void testToggleGroupProperty() {
        var property = control.toggleGroupProperty();
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo("toggleGroup");
    }

    @Test
    public void testSetAnimationDuration() {
        var customDuration = Duration.millis(300);
        control.setAnimationDuration(customDuration);
        assertThat(control.getAnimationDuration()).isEqualTo(customDuration);
    }

    @Test
    public void testSetAnimationDurationToNullDefaultsToZero() {
        control.setAnimationDuration(null);
        assertThat(control.getAnimationDuration()).isEqualTo(Duration.ZERO);
    }

    @Test
    public void testAnimationDurationProperty() {
        var property = control.animationDurationProperty();
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo("animationDuration");
    }

    @Test
    public void testSegmentsListIsModifiable() {
        assertThat(control.getSegments()).isInstanceOf(javafx.collections.ObservableList.class);
        control.getSegments().add(new ToggleLabel("First"));
        control.getSegments().add(new ToggleLabel("Second"));
        assertThat(control.getSegments()).hasSize(2);
    }
}
