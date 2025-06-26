/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.event.Event;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

public class TabLineBehavior extends BehaviorBase<TabLine, TabLineSkin> {

    public TabLineBehavior(TabLine control, TabLineSkin skin) {
        super(control, skin);

        control.setOnKeyPressed(this::createKeyPressedListener);
        control.setOnMousePressed(e -> getControl().requestFocus());
    }

    protected void createKeyPressedListener(KeyEvent e) {
        if (e.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (e.getCode()) {
                case UP -> selectPreviousTab();
                case DOWN -> selectNextTab();
                case LEFT -> rtl(getControl(), this::selectNextTab, this::selectPreviousTab);
                case RIGHT -> rtl(getControl(), this::selectPreviousTab, this::selectNextTab);
                case HOME -> {
                    if (getControl().isFocused()) {
                        moveSelection(-1, 1);
                    }
                }
                case END -> {
                    if (getControl().isFocused()) {
                        moveSelection(getControl().getTabs().size(), -1);
                    }
                }
                case TAB -> {
                    if (e.isControlDown() && e.isShiftDown()) {
                        selectPreviousTab();
                    } else if (e.isControlDown()) {
                        selectNextTab();
                    }
                }
            }
        }
    }

    protected void rtl(Node node, Runnable rtlMethod, Runnable nonRtlMethod) {
        if (Objects.requireNonNull(node.getEffectiveNodeOrientation()) == NodeOrientation.RIGHT_TO_LEFT) {
            rtlMethod.run();
        } else {
            nonRtlMethod.run();
        }
    }

    //=========================================================================

    public void selectTab(Tab tab) {
        getControl().getSelectionModel().select(tab);
    }

    public void selectPreviousTab() {
        moveSelection(-1);
    }

    public void selectNextTab() {
        moveSelection(1);
    }

    public void closeTab(Tab tab) {
        TabLine tabLine = getControl();

        // only switch to another tab if the selected tab is the one we're closing
        int index = tabLine.getTabs().indexOf(tab);
        if (index != -1) {
            tabLine.getTabs().remove(index);
        }

        if (tab.getOnClosed() != null) {
            Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
        }
    }

    public boolean canCloseTab(Tab tab) {
        // a tab can be closed is close request event wasn't consumed by user specified event handler
        var event = new Event(tab, tab, Tab.TAB_CLOSE_REQUEST_EVENT);
        Event.fireEvent(tab, event);
        return !event.isConsumed();
    }

    @Override
    public void dispose() {
        getControl().setOnKeyPressed(null);
        getControl().setOnMousePressed(null);
        super.dispose();
    }

    //=========================================================================

    protected SelectionModel<Tab> getSelectionModel() {
        return getControl().getSelectionModel();
    }

    protected void moveSelection(int delta) {
        int start = getSelectionModel().getSelectedIndex();
        moveSelection(start, delta);
    }

    protected void moveSelection(int start, int delta) {
        if (getControl().getTabs().isEmpty()) {
            return;
        }

        int index = getControl().getNextSelectedTabIndex(start + delta);
        if (index > -1) {
            getSelectionModel().select(index);
        }

        getControl().requestFocus();
    }
}
