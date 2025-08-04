/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package atlantafx.base.controls;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

import java.util.function.Consumer;

/**
 * Base implementation class for defining the visual representation of user
 * interface controls that need to handle platform events and therefore can take
 * advantage of using the Behavior API.
 *
 * <p>Note: This is an excerpt of the private Behavior API from the JavaFX codebase.
 * It serves as a compatibility layer for implementing certain controls, although it
 * can also be useful for new controls.
 */
public abstract class BehaviorSkinBase<C extends Control, B extends BehaviorBase<C, ?>> extends SkinBase<C> {

    protected B behavior; // late non-null

    /**
     * Constructor for all BehaviorSkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected BehaviorSkinBase(C control) {
        super(control);
        behavior = createDefaultBehavior();
    }

    /**
     * An abstract method for creating the behavior instance to be used by this skin.
     */
    public abstract B createDefaultBehavior();

    /**
     * Gets the control associated with this skin.
     *
     * @return The control for this Skin.
     */
    public C getControl() {
        return getSkinnable();
    }

    /**
     * Gets the behavior associated with this skin.
     *
     * @return The behavior for this skin.
     */
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
    @SuppressWarnings("ALL")
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
