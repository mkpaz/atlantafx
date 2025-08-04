/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package atlantafx.base.controls;

import atlantafx.base.util.PasswordTextFormatter;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import org.jspecify.annotations.Nullable;

/**
 * A convenience wrapper for instantiating a {@link CustomTextField}
 * with a {@code PasswordTextFormatter}. For additional info refer to the
 * {@link PasswordTextFormatter} documentation.
 */
public class PasswordTextField extends CustomTextField {

    protected final ReadOnlyObjectWrapper<PasswordTextFormatter> formatter
        = new ReadOnlyObjectWrapper<>(this, "formatter");

    /**
     * Creates an empty PasswordTextField.
     */
    public PasswordTextField() {
        this("", PasswordTextFormatter.BULLET);
    }

    /**
     * Creates a PasswordTextField with initial text content.
     *
     * @param text A string for text content.
     */
    public PasswordTextField(@Nullable @NamedArg("text") String text) {
        this(text, PasswordTextFormatter.BULLET);
    }

    /**
     * Creates a PasswordTextField with initial text content and bullet character.
     *
     * @param text   A string for text content.
     * @param bullet A bullet character to mask the password string.
     */
    public PasswordTextField(@Nullable @NamedArg("text") String text,
                             @NamedArg("bullet") char bullet) {
        super(text);
        formatter.set(PasswordTextFormatter.create(this, bullet));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * See {@link PasswordTextFormatter#passwordProperty()}.
     */
    public ReadOnlyStringProperty passwordProperty() {
        return formatter.get().passwordProperty();
    }

    /**
     * See {@link PasswordTextFormatter#getPassword()}.
     */
    public String getPassword() {
        return formatter.get().getPassword();
    }

    /**
     * See {@link PasswordTextFormatter#revealPasswordProperty()}.
     */
    public BooleanProperty revealPasswordProperty() {
        return formatter.get().revealPasswordProperty();
    }

    /**
     * See {@link PasswordTextFormatter#getRevealPassword()}.
     */
    public boolean getRevealPassword() {
        return formatter.get().getRevealPassword();
    }

    /**
     * See {@link PasswordTextFormatter#setRevealPassword(boolean)}.
     */
    public void setRevealPassword(boolean reveal) {
        formatter.get().setRevealPassword(reveal);
    }
}
