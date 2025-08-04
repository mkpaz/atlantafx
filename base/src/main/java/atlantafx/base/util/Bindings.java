/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Function;

public final class Bindings {

    public static <T, R> void bindContent(List<R> targetList,
                                          ObservableList<? extends T> sourceList,
                                          Function<T, R> mapper) {
        checkParameters(targetList, sourceList);

        var contentBinding = new ListContentBinding<>(targetList, mapper);
        if (targetList instanceof ObservableList<R> observableList) {
            observableList.setAll(map(sourceList, mapper));
        } else {
            targetList.clear();
            targetList.addAll(sourceList.stream().map(mapper).toList());
        }

        sourceList.removeListener(contentBinding);
        sourceList.addListener(contentBinding);
    }

    //=========================================================================

    private static void checkParameters(@Nullable Object property1, @Nullable Object property2) {
        if ((property1 == null) || (property2 == null)) {
            throw new NullPointerException("Both parameters must be specified");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind object to itself");
        }
    }

    private static <T, R> List<R> map(List<? extends T> list, Function<T, R> mapper) {
        return list.stream().map(mapper).toList();
    }

    //=========================================================================

    private static class ListContentBinding<T, R> implements ListChangeListener<T>, WeakListener {

        protected final WeakReference<List<R>> listRef;
        protected final Function<T, R> mapper;

        public ListContentBinding(List<R> list, Function<T, R> mapper) {
            this.listRef = new WeakReference<>(list);
            this.mapper = mapper;
        }

        @Override
        public void onChanged(Change<? extends T> change) {
            final List<R> list = listRef.get();
            if (list == null) {
                change.getList().removeListener(this);
            } else {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        list.subList(change.getFrom(), change.getTo()).clear();
                        list.addAll(
                            change.getFrom(),
                            map(change.getList().subList(change.getFrom(), change.getTo()), mapper)
                        );
                    } else {
                        if (change.wasRemoved()) {
                            list.subList(
                                change.getFrom(),
                                change.getFrom() + change.getRemovedSize()
                            ).clear();
                        }
                        if (change.wasAdded()) {
                            list.addAll(
                                change.getFrom(),
                                map(change.getAddedSubList(), mapper)
                            );
                        }
                    }
                }
            }
        }

        @Override
        public boolean wasGarbageCollected() {
            return listRef.get() == null;
        }

        @Override
        public int hashCode() {
            final List<R> list = listRef.get();
            return (list == null) ? 0 : list.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            final List<R> ourList = listRef.get();
            if (ourList == null) {
                return false;
            }

            if (obj instanceof ListContentBinding<?, ?> other) {
                final List<?> theirList = other.listRef.get();
                return ourList == theirList;
            }

            return false;
        }
    }
}
