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

import atlantafx.base.util.MaskChar;
import atlantafx.base.util.MaskTextFormatter;
import atlantafx.base.util.SimpleMaskChar;
import java.util.List;
import java.util.Objects;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jspecify.annotations.Nullable;

/**
 * A convenience wrapper for instantiating a {@link CustomTextField} with a
 * {@code MaskTextFormatter}. For additional info refer to the
 * {@link MaskTextFormatter} documentation.
 */
public class MaskTextField extends CustomTextField {

    /**
     * The whole dancing around the editable mask property is solely due to SceneBuilder
     * not works without no-arg constructor. It requires to make formatter value mutable
     * as well, which is not really tested and never intended to be supported. Also, since
     * the formatter property is not bound to the text field formatter property, setting the
     * latter manually can lead to memory leak.
     */
    protected final StringProperty mask = new SimpleStringProperty(this, "mask");

    protected final ReadOnlyObjectWrapper<@Nullable MaskTextFormatter> formatter =
        new ReadOnlyObjectWrapper<>(this, "formatter");

    /**
     * Creates an empty MaskTextField.
     */
    public MaskTextField() {
        super("");
        init();
    }

    /**
     * Creates an empty MaskTextField with the specified input mask.
     *
     * <p>The input mask is specified as a string that must follow the
     * rules described in the {@link MaskTextFormatter} documentation.
     *
     * @param mask The input mask.
     */
    public MaskTextField(@NamedArg("mask") String mask) {
        this("", mask);
    }

    /**
     * Creates a MaskTextField with initial text content and the specified input mask.
     *
     * <p>The input mask is specified as a string that must follow the
     * rules described in the {@link MaskTextFormatter} documentation.
     *
     * @param text A string for text content.
     * @param mask An input mask.
     */
    private MaskTextField(@NamedArg("text") @Nullable String text,
                          @NamedArg("mask") String mask) {
        super(Objects.requireNonNullElse(text, ""));

        formatter.set(MaskTextFormatter.create(this, mask));
        setMask(mask); // set mask only after creating a formatter, for validation
        init();
    }

    /**
     * Creates a MaskTextField with initial text content and the specified input mask.
     *
     * <p>The input mask is specified as a list of {@code MaskChar}. You can use
     * the {@link SimpleMaskChar} as the default implementation.
     *
     * @param text A string for text content.
     * @param mask An input mask.
     */
    public MaskTextField(@Nullable String text, List<MaskChar> mask) {
        super(Objects.requireNonNullElse(text, ""));

        formatter.set(MaskTextFormatter.create(this, mask));
        setMask(null);
        init();
    }

    protected void init() {
        mask.addListener((obs, old, val) -> {
            // this will replace the current text value with placeholder mask,
            // so, neither text no prompt won't be shown in the SceneBuilder
            formatter.set(val != null ? MaskTextFormatter.create(this, val) : null);
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the input mask.
     *
     * <p>Note that the MaskTextField allows for specifying the input mask as either a string
     * or a list of {@code MaskChar}. These formats cannot be converted to one another. Therefore,
     * if the input mask was specified as a list of {@code MaskChar}, this property will return
     * null value.
     */
    public StringProperty maskProperty() {
        return mask;
    }

    public @Nullable String getMask() {
        return mask.get();
    }

    public void setMask(@Nullable String mask) {
        this.mask.set(mask);
    }
}
