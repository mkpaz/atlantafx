package atlantafx.base.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NullMarked
class BindingsTest {

    private final Function<Integer, String> mapper = String::valueOf;

    @Nested
    class BindContentTest {

        @Test
        @DisplayName("should populate target list on bind")
        void testPopulateRegularListOnBind() {
            ObservableList<Integer> source = FXCollections.observableArrayList(1, 2, 3);
            List<String> target = new ArrayList<>();

            Bindings.bindContent(target, source, mapper);

            assertThat(target).containsExactly("1", "2", "3");
        }

        @Test
        @DisplayName("should clear existing target content and sync on bind")
        void testClearAndPopulateExistingTarget() {
            ObservableList<Integer> source = FXCollections.observableArrayList(10, 20);
            List<String> target = new ArrayList<>(List.of("old1", "old2"));

            Bindings.bindContent(target, source, mapper);

            assertThat(target).containsExactly("10", "20");
        }

        @Test
        @DisplayName("should use setAll when target is an ObservableList")
        void testSyncObservableListTarget() {
            ObservableList<Integer> source = FXCollections.observableArrayList(100, 200);
            ObservableList<String> target = FXCollections.observableArrayList("stale");

            Bindings.bindContent(target, source, mapper);

            assertThat(target).containsExactly("100", "200");
        }

        @Test
        @DisplayName("should reflect items added to source")
        void testSyncAddOperation() {
            ObservableList<Integer> source = FXCollections.observableArrayList(1, 2);
            List<String> target = new ArrayList<>();
            Bindings.bindContent(target, source, mapper);

            source.add(3);
            source.add(1, 99);

            assertThat(target).containsExactly("1", "99", "2", "3");
        }

        @Test
        @DisplayName("should reflect items removed from source")
        void testSyncRemoveOperation() {
            ObservableList<Integer> source = FXCollections.observableArrayList(1, 2, 3, 4);
            List<String> target = new ArrayList<>();
            Bindings.bindContent(target, source, mapper);

            source.remove(Integer.valueOf(2));
            source.removeFirst();

            assertThat(target).containsExactly("3", "4");
        }

        @Test
        @DisplayName("should reflect replaced items in source")
        void testSyncSetOperation() {
            ObservableList<Integer> source = FXCollections.observableArrayList(10, 20, 30);
            List<String> target = new ArrayList<>();
            Bindings.bindContent(target, source, mapper);

            source.set(1, 99);

            assertThat(target).containsExactly("10", "99", "30");
        }

        @Test
        @DisplayName("should reflect clear operation on source")
        void testSyncClearOperation() {
            ObservableList<Integer> source = FXCollections.observableArrayList(1, 2, 3);
            List<String> target = new ArrayList<>();
            Bindings.bindContent(target, source, mapper);

            source.clear();

            assertThat(target).isEmpty();
        }

        @Test
        @DisplayName("should reflect permutations (sort) in source")
        void testSyncPermutation() {
            ObservableList<Integer> source = FXCollections.observableArrayList(3, 1, 2);
            List<String> target = new ArrayList<>();
            Bindings.bindContent(target, source, mapper);

            FXCollections.sort(source);

            assertThat(target).containsExactly("1", "2", "3");
        }

        @Test
        @DisplayName("should throw if target is null")
        @SuppressWarnings("all")
        void testThrowWhenTargetIsNull() {
            ObservableList<Integer> source = FXCollections.observableArrayList(1);

            assertThatThrownBy(() -> Bindings.bindContent(null, source, mapper))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Both parameters must be specified");
        }

        @Test
        @DisplayName("should throw if source is null")
        @SuppressWarnings("all")
        void testThrowWhenSourceIsNull() {
            List<String> target = new ArrayList<>();

            assertThatThrownBy(() -> Bindings.bindContent(target, null, mapper))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Both parameters must be specified");
        }

        @Test
        @DisplayName("should throw when binding list to itself")
        void testThrowWhenBindingSameList() {
            ObservableList<Object> sameList = FXCollections.observableArrayList();

            assertThatThrownBy(() -> Bindings.bindContent(sameList, sameList, Object::toString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot bind object to itself");
        }


        @Test
        @DisplayName("should automatically detach listener when target is garbage collected")
        @SuppressWarnings("all")
        void testUnbindWhenTargetIsGarbageCollected() {
            ObservableList<Integer> source = FXCollections.observableArrayList(1, 2);
            List<String> target = new ArrayList<>();
            WeakReference<List<String>> targetWeakRef = new WeakReference<>(target);

            Bindings.bindContent(target, source, mapper);

            // dereference target to allow Garbage Collection
            target = null;

            System.gc();
            assertThat(targetWeakRef.get()).isNull();

            // modifying source should not throw NullPointerException
            source.add(3);
        }
    }
}