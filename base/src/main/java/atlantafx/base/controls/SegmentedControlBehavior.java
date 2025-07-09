/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.List;
import java.util.Objects;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.input.KeyEvent;

/**
 * The default behavior for the {@link SegmentedControl}.
 */
public class SegmentedControlBehavior extends BehaviorBase<SegmentedControl, SegmentedControlSkin> {

    public SegmentedControlBehavior(SegmentedControl control, SegmentedControlSkin skin) {
        super(control, skin);

        control.setOnKeyPressed(this::createKeyPressedListener);
    }

    protected void createKeyPressedListener(KeyEvent e) {
        if (e.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (e.getCode()) {
                case UP -> selectPrevious();
                case DOWN -> selectNext();
                case LEFT -> rtl(getControl(), this::selectNext, this::selectPrevious);
                case RIGHT -> rtl(getControl(), this::selectPrevious, this::selectNext);
                case HOME -> {
                    if (getControl().isFocused() || getControl().isFocusWithin()) {
                        selectFirst();
                    }
                }
                case END -> {
                    if (getControl().isFocused() || getControl().isFocusWithin()) {
                        selectLast();
                    }
                }
                case TAB -> {
                    if (e.isControlDown() && e.isShiftDown()) {
                        selectPrevious();
                    } else if (e.isControlDown()) {
                        selectNext();
                    }
                }
            }
        }

        e.consume();
    }

    protected void rtl(Node node, Runnable rtlMethod, Runnable nonRtlMethod) {
        if (Objects.requireNonNull(node.getEffectiveNodeOrientation()) == NodeOrientation.RIGHT_TO_LEFT) {
            rtlMethod.run();
        } else {
            nonRtlMethod.run();
        }
    }

    protected List<ToggleLabel> getSegments() {
        return getControl().getSegments();
    }

    protected int getSelectedIndex() {
        Toggle toggle = getControl().getToggleGroup().getSelectedToggle();
        if (toggle == null) {
            return -1;
        }

        return toggle instanceof ToggleLabel label ? getSegments().indexOf(label) : -1;
    }

    //=========================================================================

    public void selectFirst() {
        if (!getSegments().isEmpty()) {
            select(getControl().getSegments().getFirst());
        }
    }

    public void selectLast() {
        if (!getSegments().isEmpty()) {
            select(getControl().getSegments().getLast());
        }
    }

    public void selectPrevious() {
        if (getSegments().isEmpty()) {
            return;
        }

        var selectedIndex = getSelectedIndex();
        if (selectedIndex != 0) {
            select(getSegments().get(selectedIndex - 1));
        } else if (getSegments().size() > 1) {
            selectLast();
        }
    }

    public void selectNext() {
        if (getSegments().isEmpty()) {
            return;
        }

        var selectedIndex = getSelectedIndex();
        if (selectedIndex != getSegments().size() - 1) {
            select(getSegments().get(selectedIndex + 1));
        } else if (getSegments().size() > 1) {
            selectFirst();
        }
    }

    public void select(Toggle toggle) {
        if (!toggle.isSelected()) {
            toggle.setSelected(true);
        }
    }

    @Override
    public void dispose() {
        getControl().setOnKeyPressed(null);
        super.dispose();
    }
}
