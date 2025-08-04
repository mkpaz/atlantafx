/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.Arrays;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * The {@code SegmentedControl} is a group of labels that are joined together
 * in a single group, maintaining the selection model via the {@link #toggleGroupProperty()}.
 *
 * <p>This component offers an alternative flavor to {@link TabLine} or {@link TabPane}, but
 * it does not support features such as closing tabs (labels) or overflow handling.
 */
public class SegmentedControl extends Control {

    protected static final Duration DEFAULT_ANIMATION_DURATION = Duration.millis(150);

    public SegmentedControl() {
        super();

        getStyleClass().add("segmented-control");
        setFocusTraversable(false);
    }

    public SegmentedControl(String @Nullable... segments) {
        this();

        if (segments != null) {
            this.segments.setAll(
                Arrays.stream(segments).map(ToggleLabel::new).toList()
            );
        }
    }

    public SegmentedControl(ToggleLabel @Nullable... segments) {
        this();

        if (segments != null) {
            this.segments.setAll(segments);
        }
    }

    /**
     * Creates a SegmentedButton with the provided segments inserted into it.
     */
    public SegmentedControl(@Nullable ObservableList<ToggleLabel> segments) {
        this();

        if (segments != null && !segments.isEmpty()) {
            this.segments.setAll(segments);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new SegmentedControlSkin(this);
    }

    //=========================================================================
    // Properties
    //=========================================================================

    /**
     * Returns the list of buttons that this SegmentedButton consists of.
     */
    public final ObservableList<ToggleLabel> getSegments() {
        return segments;
    }

    protected final ObservableList<ToggleLabel> segments = FXCollections.observableArrayList();

    /**
     * The ToggleGroup that is used internally maintain the selection.
     */
    public ObjectProperty<ToggleGroup> toggleGroupProperty() {
        return toggleGroup;
    }

    protected final ObjectProperty<ToggleGroup> toggleGroup = new SimpleObjectProperty<>(
        this, "toggleGroup", new ToggleGroup()
    );

    public ToggleGroup getToggleGroup() {
        return toggleGroupProperty().getValue();
    }

    public void setToggleGroup(ToggleGroup toggleGroup) {
        toggleGroupProperty().setValue(toggleGroup);
    }

    /**
     * Specifies the duration for animating the transition between toggles.
     * Setting this value to zero or null disables the animation.
     */
    public ObjectProperty<Duration> animationDurationProperty() {
        return animationDuration;
    }

    protected final ObjectProperty<Duration> animationDuration = new SimpleObjectProperty<>(
        this, "animationDuration", DEFAULT_ANIMATION_DURATION
    );

    public Duration getAnimationDuration() {
        return animationDurationProperty().getValue();
    }

    public void setAnimationDuration(@Nullable Duration duration) {
        animationDurationProperty().setValue(Objects.requireNonNullElse(duration, Duration.ZERO));
    }
}
