/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package atlantafx.base.controls;

import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

/**
 * Encapsulates behavior interaction logic for a skin. The main functionality
 * in BehaviorBase revolves around infrastructure for resolving events into
 * function calls. A BehaviorBase implementation will usually contain logic for
 * handling key events based on the host platform, as well as view-specific
 * functions for handling mouse and key and other input events.
 *
 * <p>Although BehaviorBase is typically used as a base class, it is not abstract and
 * several skins instantiate an instance of BehaviorBase directly.
 *
 * <p>Note: This is an excerpt of the private Behavior API from the JavaFX codebase.
 * It serves as a compatibility layer for implementing certain controls, although it
 * can also be useful for new controls.
 */
public class BehaviorBase<C extends Control, S extends SkinBase<C>> {

    private C control;
    private S skin;

    /**
     * Constructor for all BehaviorBase instances.
     *
     * @param control The control for which this Skin should attach to.
     * @param skin    The skin used by the control.
     */
    protected BehaviorBase(C control, S skin) {
        this.control = control;
        this.skin = skin;
    }

    /**
     * Gets the control associated with this behavior.
     *
     * @return The control for this behavior.
     */
    public C getControl() {
        return control;
    }

    /**
     * Gets the skin associated with this behavior.
     *
     * @return The control for this behavior.
     */
    public S getSkin() {
        return skin;
    }

    /**
     * Called from {@link SkinBase#dispose()} to clean up the behavior state.
     */
    @SuppressWarnings("ALL")
    public void dispose() {
        this.control = null;
        this.skin = null;
    }
}
