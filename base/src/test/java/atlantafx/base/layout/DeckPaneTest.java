/* SPDX-License-Identifier: MIT */

package atlantafx.base.layout;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// FIXME DeckPane test
// Something has been changed and assertions trigger before
// animation completed. Each run random tests fail.
@Disabled
@ExtendWith({JavaFXTest.class})
@NullMarked
public class DeckPaneTest {

    @Test
    public void testTopNodeOnNewlyCreatedDeck() {
        var emptyDeck = new DeckPane();
        assertThat(emptyDeck.getTopNode()).isNull();

        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);
    }

    @Test
    public void testSettingTopNode() {
        var deck = new DeckPane(
            new Rectangle(10, 10),
            new Rectangle(20, 20),
            new Rectangle(30, 30)
        );

        assertThat(deck.getTopNode()).isEqualTo(deck.getChildren().get(2));

        deck.setTopNode(deck.getChildren().get(0));
        assertThat(deck.getTopNode()).isEqualTo(deck.getChildren().get(0));

        deck.resetTopNode();
        assertThat(deck.getTopNode()).isEqualTo(deck.getChildren().get(2));
    }

    @Test
    public void testViewOrderRestoredForRemovedNodes() {
        var deck = new DeckPane(
            new Rectangle(10, 10),
            new Rectangle(20, 20),
            new Rectangle(30, 30)
        );
        deck.setAnimationDuration(Duration.ZERO);
        var node = deck.getChildren().get(1);

        deck.swipeUp(node);
        assertThat(deck.getTopNode()).isEqualTo(node);
        assertThat(node.getViewOrder()).isEqualTo(DeckPane.Z_DECK_TOP);

        deck.getChildren().remove(node);
        assertThat(deck.getChildren()).doesNotContain(node);
        assertThat(node.getViewOrder()).isEqualTo(DeckPane.Z_DEFAULT);
    }

    @Test
    public void testSwipeUp() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.swipeUp(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.swipeUp(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.swipeUp(newNode));
    }

    @Test
    public void testSwipeDown() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.swipeDown(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.swipeDown(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.swipeDown(newNode));
    }

    @Test
    public void testSwipeLeft() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.swipeLeft(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.swipeLeft(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.swipeLeft(newNode));
    }

    @Test
    public void testSwipeRight() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.swipeRight(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.swipeRight(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.swipeRight(newNode));
    }

    @Test
    public void testSlideUp() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.slideUp(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.slideUp(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.slideUp(newNode));
    }

    @Test
    public void testSlideDown() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.slideDown(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.slideDown(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.slideDown(newNode));
    }

    @Test
    public void testSlideLeft() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.slideLeft(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.slideLeft(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.slideLeft(newNode));
    }

    @Test
    public void testSlideRight() {
        var deck = new TestDeck();
        assertThat(deck.pane.getTopNode()).isEqualTo(deck.r1);

        deck.runAndAssert(deck.r2, pane -> pane.slideRight(deck.r2));
        deck.runAndAssert(deck.r3, pane -> pane.slideRight(deck.r3));

        var newNode = new Rectangle(40, 40);
        deck.runAndAssert(newNode, pane -> pane.slideRight(newNode));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class TestDeck {

        public final DeckPane pane;
        public final Rectangle r1;
        public final Rectangle r2;
        public final Rectangle r3;

        public int showCounter;
        public int hideCounter;

        public TestDeck() {
            // size works as node identifier
            r1 = new Rectangle(10, 10);
            r2 = new Rectangle(20, 20);
            r3 = new Rectangle(30, 30);

            pane = new DeckPane(r3, r2, r1);
            // to not make a pause between tests
            pane.setAnimationDuration(Duration.ZERO);
            pane.setBeforeShowCallback(node -> showCounter++);
            pane.setAfterHideCallback(node -> hideCounter++);
        }

        public void runAndAssert(Node node, Consumer<DeckPane> action) {
            var hideCounterBefore = hideCounter;
            var showCounterBefore = showCounter;
            action.accept(pane);

            assertThat(isFresh()).isTrue();
            assertThat(pane.getTopNode()).isEqualTo(node);
            assertThat(hideCounter).isEqualTo(++hideCounterBefore);
            assertThat(showCounter).isEqualTo(++showCounterBefore);

            assertThat(node.getViewOrder()).isEqualTo(DeckPane.Z_DECK_TOP);
            pane.getChildren().stream()
                .filter(child -> child != node)
                .forEach(child -> assertThat(child.getViewOrder()).isEqualTo(DeckPane.Z_DEFAULT));
        }

        /**
         * See {@link #isNodeFresh}.
         */
        public boolean isFresh() {
            return isNodeFresh(r1) && isNodeFresh(r2) && isNodeFresh(r3);
        }

        /**
         * Checks that all node properties involved into animation
         * have been reset to its default state.
         */
        public boolean isNodeFresh(Node node) {
            return node.getClip() == null && node.getTranslateX() == 0 && node.getTranslateY() == 0;
        }
    }
}
