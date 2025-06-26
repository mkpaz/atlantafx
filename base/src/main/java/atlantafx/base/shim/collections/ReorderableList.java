/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
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

package atlantafx.base.shim.collections;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.ListIterator;

/**
 * A slightly modified version of {@code com.sun.javafx.scene.control.TabObservableList}.
 */
public class ReorderableList<E> extends ObservableListWrapper<E> {

    private final List<E> backingList;

    public ReorderableList(List<E> backingList) {
        super(backingList);

        if (backingList instanceof ObservableList<E>) {
            throw new IllegalArgumentException("backingList must not be an observable list");
        }

        this.backingList = backingList;
    }

    public void reorder(E from, E to) {
        if (from == to) {
            return;
        }

        reorder(backingList.indexOf(from), backingList.indexOf(to));
    }

    @SuppressWarnings("unchecked")
    public void reorder(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex == toIndex) {
            return;
        }

        E[] arr = backingList.toArray((E[]) new Object[backingList.size()]);
        int direction = (toIndex - fromIndex) / Math.abs(toIndex - fromIndex);

        for (int i = fromIndex; i != toIndex; i += direction) {
            arr[i] = arr[i + direction];
        }
        arr[toIndex] = backingList.get(fromIndex);

        // update the list with reordered array
        ListIterator<E> iterator = backingList.listIterator();
        for (int i = 0; i < backingList.size(); i++) {
            iterator.next();
            iterator.set(arr[i]);
        }

        // fire permutation change event
        int permSize = Math.abs(toIndex - fromIndex) + 1;
        int[] perm = new int[permSize];
        int permFrom = direction > 0 ? fromIndex : toIndex;
        int permTo = direction < 0 ? fromIndex : toIndex;
        for (int i = 0; i < permSize; ++i) {
            perm[i] = i + permFrom;
        }

        fireChange(new NonIterableChange.SimplePermutationChange<>(permFrom, permTo + 1, perm, this));
    }
}
