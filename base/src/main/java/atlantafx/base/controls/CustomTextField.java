/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2013, 2015, ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import org.jspecify.annotations.Nullable;

/**
 * A base class for people wanting to customize a {@link TextField}
 * to contain nodes inside the text field itself, without being on top
 * of the users typed-in text.
 */
public class CustomTextField extends TextField {

    /**
     * Creates an empty CustomTextField.
     */
    public CustomTextField() {
        getStyleClass().add("custom-text-field");
    }

    /**
     * Creates a CustomTextField with initial text content.
     *
     * @param text A string for text content.
     */
    public CustomTextField(@Nullable String text) {
        this();
        setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomTextFieldSkin(this) {
            @Override
            public ObjectProperty<Node> leftProperty() {
                return CustomTextField.this.leftProperty();
            }

            @Override
            public ObjectProperty<Node> rightProperty() {
                return CustomTextField.this.rightProperty();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the {@link Node} that is placed on the left of the text field.
     */
    public final ObjectProperty<@Nullable Node> leftProperty() {
        return left;
    }

    private final ObjectProperty<@Nullable Node> left = new SimpleObjectProperty<>(this, "left");

    /**
     * Returns the {@link Node} that is placed on the left of the text field.
     */
    public final @Nullable Node getLeft() {
        return left.get();
    }

    /**
     * Sets the {@link Node} that is placed on the left of the text field.
     */
    public final void setLeft(@Nullable Node value) {
        left.set(value);
    }

    /**
     * Represents the {@link Node} that is placed on the right of the text field.
     */
    public final ObjectProperty<@Nullable Node> rightProperty() {
        return right;
    }

    private final ObjectProperty<@Nullable Node> right = new SimpleObjectProperty<>(this, "right");

    /**
     * Returns the {@link Node} that is placed on the right of the text field.
     */
    public final @Nullable Node getRight() {
        return right.get();
    }

    /**
     * Sets the {@link Node} that is placed on the right of the text field.
     */
    public final void setRight(@Nullable Node value) {
        right.set(value);
    }
}
