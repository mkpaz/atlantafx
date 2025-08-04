/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.Bindings;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;


public class TabMenuButton extends Button {

    protected final TabLine tabLine;
    protected final ContextMenu contextMenu = new ContextMenu();
    protected final ToggleGroup toggleGroup = new ToggleGroup();

    public TabMenuButton(TabLine tabLine) {
        this(tabLine, null);
    }

    public TabMenuButton(TabLine tabLine, @Nullable Function<Tab, MenuItem> mapper) {
        super();

        this.tabLine = tabLine;

        if (mapper == null) {
            mapper = this::createRadioMenuItemMapper;
        }

        var icon = new StackPane();
        icon.getStyleClass().setAll("tab-menu-icon");
        setGraphic(icon);

        getStyleClass().addAll(Styles.FLAT, "tab-menu-button");
        setOnAction(o -> {
            beforeShow();
            contextMenu.show(this, Side.BOTTOM, 0, 0);
        });

        Bindings.bindContent(
            contextMenu.getItems(),
            tabLine.getTabs(),
            mapper
        );
    }

    protected void beforeShow() {
        for (MenuItem mi : contextMenu.getItems()) {
            if (mi instanceof RadioMenuItem rmi) {
                var selectedTab = tabLine.getSelectionModel().getSelectedItem();
                //noinspection ConstantValue, Intellij is being stupid
                rmi.setSelected(selectedTab != null
                        && mi.getUserData() != null
                        && selectedTab.hashCode() == mi.getUserData().hashCode()
                );
            }
        }
    }

    protected RadioMenuItem createRadioMenuItemMapper(Tab tab) {
        // don't use graphic from tab as it can only be linked to a single node
        var mi = new RadioMenuItem(tab.getText());
        mi.setOnAction(o -> {
            if (tab.getTabLine() != null) {
                tab.getTabLine().getSelectionModel().select(tab);
            }
        });
        mi.setUserData(tab.hashCode());
        mi.setToggleGroup(toggleGroup);

        return mi;
    }
}
