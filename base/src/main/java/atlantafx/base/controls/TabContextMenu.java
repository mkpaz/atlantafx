/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.jspecify.annotations.Nullable;

/**
 * An auxiliary class that allows for the reuse of a single {@link ContextMenu} instance
 * across multiple tabs. The user can obtain the {@link Tab} for which the context menu
 * was invoked from the {@link #ownerTabProperty()}.
 */
public class TabContextMenu extends ContextMenu {

    protected @Nullable ReadOnlyObjectWrapper<Tab> ownerTab;

    public TabContextMenu() {
        super();
    }

    public TabContextMenu(MenuItem... items) {
        super(items);
    }

    @Override
    public void hide() {
        updateOwnerTab(null);
        super.hide();
    }

    //=========================================================================

    public final ReadOnlyObjectProperty<@Nullable Tab> ownerTabProperty() {
        return ownerTabPropertyImpl().getReadOnlyProperty();
    }

    public @Nullable Tab getOwnerTab() {
        if (ownerTab == null) {
            return null;
        }
        return ownerTabPropertyImpl().get();
    }

    protected ReadOnlyObjectWrapper<@Nullable Tab> ownerTabPropertyImpl() {
        if (ownerTab == null) {
            ownerTab = new ReadOnlyObjectWrapper<>();
        }
        return ownerTab;
    }

    protected void updateOwnerTab(@Nullable Tab value) {
        ownerTabPropertyImpl().set(value);
    }
}
