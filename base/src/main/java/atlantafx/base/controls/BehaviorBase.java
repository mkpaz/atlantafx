/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

public abstract class BehaviorBase<C extends Control, S extends SkinBase<C>> {

    private C control;
    private S skin;

    protected BehaviorBase(C control, S skin) {
        this.control = control;
        this.skin = skin;
    }

    public C getControl() {
        return control;
    }

    public S getSkin() {
        return skin;
    }

    /**
     * Called from {@link SkinBase#dispose()} to clean up the behavior state.
     */
    public void dispose() {
        this.control = null;
        this.skin = null;
    }
}
