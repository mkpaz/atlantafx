/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import java.util.function.Consumer;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

public abstract class BehaviorSkinBase<C extends Control, B extends BehaviorBase<C, ?>> extends SkinBase<C> {

    protected B behavior;

    protected BehaviorSkinBase(C control) {
        super(control);
        behavior = createDefaultBehavior();
    }

    public abstract B createDefaultBehavior();

    public C getControl() {
        return getSkinnable();
    }

    public B getBehavior() {
        return behavior;
    }

    /**
     * Unbinds all properties and removes any listeners before disposing the skin.
     * There's no need to remove listeners, which has been registered using
     * {@link SkinBase#registerChangeListener(ObservableValue, Consumer)} method,
     * because it will be done automatically from dispose method.
     */
    protected void unregisterListeners() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        unregisterListeners();

        // unregister weak listeners and remove reference to the control
        super.dispose();

        // cleanup the behavior
        if (behavior != null) {
            behavior.dispose();
            behavior = null;
        }
    }
}
