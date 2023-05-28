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
import java.util.List;
import java.util.Objects;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.Nullable;

/**
 * This is a convenience wrapper for instantiating a {@link CustomTextField} with a
 * {@code MaskTextFormatter}. For additional info refer to the {@link MaskTextFormatter}
 * docs.
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

    protected final ReadOnlyObjectWrapper<MaskTextFormatter> formatter =
        new ReadOnlyObjectWrapper<>(this, "formatter");

    public MaskTextField() {
        super("");
        init();
    }

    public MaskTextField(@NamedArg("mask") String mask) {
        this("", mask);
    }

    private MaskTextField(@NamedArg("text") String text,
                          @NamedArg("mask") String mask) {
        super(Objects.requireNonNullElse(text, ""));

        formatter.set(MaskTextFormatter.create(this, mask));
        setMask(mask); // set mask only after creating a formatter, for validation
        init();
    }

    public MaskTextField(String text, List<MaskChar> mask) {
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
