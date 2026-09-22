package atlantafx.base.util;

import atlantafx.base.util.Animations.ResetState;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class ResetStateTest {

    @Test
    @DisplayName("should capture opacity and restore it")
    void testStoreAndRestoreOpacity() {
        var node = new Rectangle();
        node.setOpacity(0.42);

        var state = new ResetState();
        state.storeOpacity(node);

        node.setOpacity(0.0);
        state.restore(node);

        assertEquals(0.42, node.getOpacity(), 1e-6);
    }

    @Test
    @DisplayName("should fallback to default opacity when default captured")
    void testStoreDefaultOpacityFallback() {
        var node = new Rectangle();
        node.setOpacity(1.0); // default value

        var state = new ResetState();
        state.storeOpacity(node);

        node.setOpacity(0.2);
        state.restore(node);

        assertEquals(1.0, node.getOpacity(), 1e-6);
    }

    @Test
    @DisplayName("should capture translates and restore them")
    void testStoreAndRestoreTranslates() {
        var node = new Rectangle();
        node.setTranslateX(10.0);
        node.setTranslateY(20.0);
        node.setTranslateZ(30.0);

        var state = new ResetState();
        state.storeTranslateX(node);
        state.storeTranslateY(node);
        state.storeTranslateZ(node);

        node.setTranslateX(100.0);
        node.setTranslateY(200.0);
        node.setTranslateZ(300.0);

        state.restore(node);

        assertEquals(10.0, node.getTranslateX(), 1e-6);
        assertEquals(20.0, node.getTranslateY(), 1e-6);
        assertEquals(30.0, node.getTranslateZ(), 1e-6);
    }

    @Test
    @DisplayName("should capture scales and restore them")
    void testStoreAndRestoreScales() {
        var node = new Rectangle();
        node.setScaleX(1.5);
        node.setScaleY(2.0);
        node.setScaleZ(0.5);

        var state = new ResetState();
        state.storeScaleX(node);
        state.storeScaleY(node);
        state.storeScaleZ(node);

        node.setScaleX(0.0);
        node.setScaleY(0.0);
        node.setScaleZ(0.0);

        state.restore(node);

        assertEquals(1.5, node.getScaleX(), 1e-6);
        assertEquals(2.0, node.getScaleY(), 1e-6);
        assertEquals(0.5, node.getScaleZ(), 1e-6);
    }

    @Test
    @DisplayName("should capture rotation angle and axis and restore them")
    void testStoreAndRestoreRotateAndAxis() {
        var node = new Rectangle();
        node.setRotate(90.0);
        node.setRotationAxis(Rotate.X_AXIS);

        var state = new ResetState();
        state.storeRotate(node);
        state.storeRotationAxis(node);

        node.setRotate(180.0);
        node.setRotationAxis(Rotate.Y_AXIS);

        state.restore(node);

        assertEquals(90.0, node.getRotate(), 1e-6);
        assertEquals(Rotate.X_AXIS, node.getRotationAxis());
    }

    @Test
    @DisplayName("should remove transform upon restoration")
    void testStoreTransformRemoval() {
        var node = new Rectangle();
        Rotate customRotate = new Rotate(45);

        var state = new ResetState();
        state.storeCustomTransform(node, customRotate);
        node.getTransforms().add(customRotate);

        assertTrue(node.getTransforms().contains(customRotate));

        state.restore(node);

        assertFalse(node.getTransforms().contains(customRotate));
    }

    @Test
    @DisplayName("should replace previous transform on new assignment")
    void testStoreTransformReplacesPrevious() {
        var node = new Rectangle();
        Rotate firstRotate = new Rotate(15);
        Rotate secondRotate = new Rotate(30);

        var state = new ResetState();

        state.storeCustomTransform(node, firstRotate);
        node.getTransforms().add(firstRotate);

        state.storeCustomTransform(node, secondRotate);
        node.getTransforms().add(secondRotate);

        assertFalse(node.getTransforms().contains(firstRotate));
        assertTrue(node.getTransforms().contains(secondRotate));

        state.restore(node);
        assertFalse(node.getTransforms().contains(secondRotate));
    }

    @Test
    @DisplayName("should reset internal mask making subsequent calls no-op")
    void testRestoreClearsMask() {
        var node = new Rectangle();
        node.setOpacity(0.5);

        var state = new ResetState();
        state.storeOpacity(node);

        node.setOpacity(0.0);
        state.restore(node);

        assertEquals(0.5, node.getOpacity(), 1e-6);

        node.setOpacity(0.1);
        state.restore(node);

        assertEquals(1.0, node.getOpacity(), 1e-6);
    }
}