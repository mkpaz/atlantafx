/* SPDX-License-Identifier: MIT */

package atlantafx.base.util;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StyleMapTest {

    Pane node;
    StyleMap styleMap;

    @BeforeEach
    void setup() {
        node = new Pane();
        styleMap = StyleMap.on(node);
    }

    @Test
    @DisplayName("on() should create a new map and cache it in node properties")
    void testOnCreatesAndCachesInstance() {
        StyleMap secondCallMap = StyleMap.on(node);

        assertThat(styleMap).isNotNull();
        assertThat(secondCallMap).isSameAs(styleMap);
    }

    @Test
    @DisplayName("get() should return value by key or null if key is absent")
    void testGet() {
        styleMap.set("-fx-pref-width", "100px");

        assertThat(styleMap.get("-fx-pref-width")).isEqualTo("100px");
        assertThat(styleMap.get("-fx-pref-height")).isNull();
    }

    @Test
    @DisplayName("set() should update attributes")
    void testSet() {
        styleMap.set("-fx-background-color", "blue");
        assertThat(styleMap.get("-fx-background-color")).isEqualTo("blue");

        // update existing property
        styleMap.set("-fx-background-color", "red");
        assertThat(styleMap.get("-fx-background-color")).isEqualTo("red");

        // passing null should remove the key
        styleMap.set("-fx-background-color", null);
        assertThat(styleMap.get("-fx-background-color")).isNull();
        assertThat(styleMap.attributes()).doesNotContainKey("-fx-background-color");
    }

    @Test
    @DisplayName("remove() should remove attribute")
    void testRemove() {
        styleMap.set("-fx-padding", "10px");
        assertThat(styleMap.get("-fx-padding")).isEqualTo("10px");

        // removing an existing key
        styleMap.remove("-fx-padding");
        assertThat(styleMap.get("-fx-padding")).isNull();

        // removing non-existent key should not throw or alter state
        styleMap.remove("-fx-padding");
        assertThat(styleMap.get("-fx-padding")).isNull();
    }

    @Test
    @DisplayName("clear() should remove all attributes")
    void testClear() {
        styleMap
            .set("-fx-font-size", "14px")
            .set("-fx-text-fill", "black");

        assertThat(styleMap.attributes()).hasSize(2);

        styleMap.clear();

        assertThat(styleMap.attributes()).isEmpty();
        assertThat(styleMap.toString()).isEmpty();

        // repeated call on empty map
        styleMap.clear();
        assertThat(styleMap.attributes()).isEmpty();
    }

    @Test
    @DisplayName("attributes() returns an unmodifiable snapshot")
    void testAttributes() {
        styleMap.set("-fx-border-width", "1px");

        Map<String, String> copy = styleMap.attributes();
        assertThat(copy).containsEntry("-fx-border-width", "1px");

        // verify immutability
        assertThatThrownBy(() -> copy.put("-fx-border-width", "2px"))
            .isInstanceOf(UnsupportedOperationException.class);

        styleMap.set("-fx-border-width", "5px");
        assertThat(copy.get("-fx-border-width")).isEqualTo("1px");
        assertThat(styleMap.get("-fx-border-width")).isEqualTo("5px");
    }

    @Test
    @DisplayName("toString() should return correct CSS")
    void testToString() {
        assertThat(styleMap.toString()).isEmpty();

        styleMap
            .set("-fx-border-color", "white")
            .set("-fx-border-width", "0");

        assertThat(styleMap.toString()).isEqualTo("-fx-border-color:white;-fx-border-width:0;");
    }

    @Test
    @DisplayName("apply() should update node style only when unbound")
    void testApply() {
        styleMap.set("-fx-rotate", "45");

        // unbound state, apply() works
        styleMap.apply();
        assertThat(node.getStyle()).isEqualTo("-fx-rotate:45;");

        styleMap.bind();
        styleMap.set("-fx-rotate", "90");

        // bound state, apply() is ignored
        styleMap.apply();
        assertThat(node.getStyle()).isEqualTo("-fx-rotate:90;");
    }

    @Test
    @DisplayName("bind() should automatically update node style")
    void testBind() {
        assertThat(styleMap.isBound()).isFalse();

        styleMap.bind();
        assertThat(styleMap.isBound()).isTrue();

        styleMap.set("-fx-opacity", "0.5");
        assertThat(node.getStyle()).isEqualTo("-fx-opacity:0.5;");

        // repeated bind() call (idempotency check)
        styleMap.bind();
        assertThat(styleMap.isBound()).isTrue();
    }

    @Test
    @DisplayName("unbind() should disconnect style map from styleProperty")
    void testUnbind() {
        styleMap
            .bind()
            .set("-fx-min-width", "100px");

        assertThat(node.getStyle()).isEqualTo("-fx-min-width:100px;");
        assertThat(styleMap.isBound()).isTrue();

        styleMap.unbind();

        assertThat(styleMap.isBound()).isFalse();
        assertThat(node.styleProperty().isBound()).isFalse();

        // automatic updates stop after unbind
        styleMap.set("-fx-min-width", "200px");
        assertThat(node.getStyle()).isEqualTo("-fx-min-width:100px;"); // style on node remains unchanged

        // apply() works again
        styleMap.apply();
        assertThat(node.getStyle()).isEqualTo("-fx-min-width:200px;");

        // repeated unbind() call is safe
        styleMap.unbind();
        assertThat(styleMap.isBound()).isFalse();
    }

    @Test
    @DisplayName("notifyMapChanged() should trigger binding recalculation")
    void testFireChanged() {
        styleMap.bind().set("-fx-pref-height", "50px");
        styleMap.fireChanged();

        // ensure binding responds and applies updated toString()
        assertThat(node.getStyle()).isEqualTo(styleMap.toString());
    }

    @Test
    @DisplayName("set() should atomically set all attributes and trigger fireChanged once")
    void testSetMap() {
        styleMap.bind();

        Map<String, String> styles = Map.of(
            "-fx-pref-width", "100px",
            "-fx-pref-height", "200px"
        );

        styleMap.set(styles);

        assertThat(styleMap.get("-fx-pref-width")).isEqualTo("100px");
        assertThat(styleMap.get("-fx-pref-height")).isEqualTo("200px");
        assertThat(node.getStyle()).contains("-fx-pref-width:100px;", "-fx-pref-height:200px;");

        // passing empty map should not trigger changes or fail
        styleMap.set(Map.of());
        assertThat(styleMap.attributes()).hasSize(2);
    }

    @Test
    @DisplayName("remove() should atomically remove multiple attributes")
    void testRemoveMultiple() {
        styleMap.bind()
            .set("-fx-pref-width", "100px")
            .set("-fx-pref-height", "200px")
            .set("-fx-padding", "10px");

        styleMap.remove("-fx-pref-width", "-fx-pref-height");

        assertThat(styleMap.get("-fx-pref-width")).isNull();
        assertThat(styleMap.get("-fx-pref-height")).isNull();
        assertThat(styleMap.get("-fx-padding")).isEqualTo("10px");
        assertThat(node.getStyle()).isEqualTo("-fx-padding:10px;");

        styleMap.remove(List.of("-fx-padding"));
        assertThat(styleMap.attributes()).isEmpty();
        assertThat(node.getStyle()).isEmpty();
    }
}