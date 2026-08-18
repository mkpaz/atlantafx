/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Function;

/**
 * Provides utility methods to create content bindings.
 */
public final class Bindings {

    /**
     * Synchronizes the content of a target list with a source observable list.
     *
     * <p>Elements from the source list are converted using the provided mapper
     * function before they are added to the target list.
     *
     * <p>Example:
     * <pre>{@code
     * ObservableList<String> source = FXCollections.observableArrayList("1", "2", "3");
     * List<Integer> target = new ArrayList<>();
     *
     * // binds target list to source list by converting Strings to Integers
     * Bindings.bindContent(target, source, Integer::parseInt);
     * }</pre>
     *
     * @param <T> the type of elements in the source list
     * @param <R> the type of elements in the target list
     * @param targetList the list that receives the transformed elements
     * @param sourceList the observable list that provides the source elements
     * @param mapper the function that converts elements from type T to type R
     * @throws NullPointerException if either targetList or sourceList is null
     * @throws IllegalArgumentException if targetList and sourceList refer to the same object
     */
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

    //*************************************************************************

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
