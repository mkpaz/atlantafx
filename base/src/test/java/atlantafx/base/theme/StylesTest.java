/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Base64;
import javafx.css.PseudoClass;
import javafx.scene.layout.Region;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

@NullMarked
public class StylesTest {

    final PseudoClass pcFirst = PseudoClass.getPseudoClass("first");
    final PseudoClass pcSecond = PseudoClass.getPseudoClass("second");
    final PseudoClass pcExcluded = PseudoClass.getPseudoClass("excluded");

    @Test
    public void testToggleStyleClassOn() {
        var node = new Region();
        node.getStyleClass().add("first");
        assertThat(node.getStyleClass()).containsExactly("first");

        Styles.toggleStyleClass(node, "second");
        assertThat(node.getStyleClass()).containsExactly("first", "second");
    }

    @Test
    public void testToggleStyleClassMultipleOn() {
        var node = new Region();
        node.getStyleClass().addAll("first", "second", "third");
        assertThat(node.getStyleClass()).containsExactly("first", "second", "third");

        Styles.toggleStyleClass(node, "fourth");
        assertThat(node.getStyleClass()).containsExactly("first", "second", "third", "fourth");
    }

    @Test
    public void testToggleStyleClassOff() {
        var node = new Region();
        node.getStyleClass().add("sole");
        assertThat(node.getStyleClass()).containsExactly("sole");

        Styles.toggleStyleClass(node, "sole");
        assertThat(node.getStyleClass()).isEmpty();
    }

    @Test
    public void testToggleStyleClassMultipleOff() {
        var node = new Region();
        node.getStyleClass().addAll("first", "second", "third");
        assertThat(node.getStyleClass()).containsExactly("first", "second", "third");

        Styles.toggleStyleClass(node, "second");
        assertThat(node.getStyleClass()).containsExactly("first", "third");
    }

    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testAddStyleClassAdds() {
        var node = new Region();
        node.getStyleClass().addAll("first");
        assertThat(node.getStyleClass()).containsExactly("first");

        Styles.addStyleClass(node, "second");
        assertThat(node.getStyleClass()).containsExactly("first", "second");
    }

    @Test
    public void testAddStyleClassExcludes() {
        var node = new Region();
        node.getStyleClass().addAll("first", "excluded");
        assertThat(node.getStyleClass()).containsExactly("first", "excluded");

        Styles.addStyleClass(node, "second", "excluded");
        assertThat(node.getStyleClass()).containsExactly("first", "second");
    }

    @Test
    public void testAddStyleClassIgnoresDuplicate() {
        var node = new Region();
        node.getStyleClass().addAll("first", "second", "excluded");
        assertThat(node.getStyleClass()).containsExactly("first", "second", "excluded");

        Styles.addStyleClass(node, "second", "excluded");
        assertThat(node.getStyleClass()).containsExactly("first", "second");
    }

    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testActivatePseudoClassActivates() {
        var node = new Region();
        node.pseudoClassStateChanged(pcFirst, true);
        assertThat(node.getPseudoClassStates()).containsExactly(pcFirst);

        Styles.activatePseudoClass(node, pcSecond);
        assertThat(node.getPseudoClassStates()).containsExactly(pcFirst, pcSecond);
    }

    @Test
    public void testActivatePseudoClassExcludes() {
        var node = new Region();
        node.pseudoClassStateChanged(pcFirst, true);
        node.pseudoClassStateChanged(pcExcluded, true);
        assertThat(node.getPseudoClassStates()).containsExactly(pcFirst, pcExcluded);

        Styles.activatePseudoClass(node, pcSecond, pcExcluded);
        assertThat(node.getPseudoClassStates()).containsExactly(pcFirst, pcSecond);
    }

    @Test
    public void testActivatePseudoClassIgnoresDuplicate() {
        var node = new Region();
        node.pseudoClassStateChanged(pcFirst, true);
        node.pseudoClassStateChanged(pcSecond, true);
        node.pseudoClassStateChanged(pcExcluded, true);
        assertThat(node.getPseudoClassStates()).containsExactly(pcFirst, pcSecond, pcExcluded);

        Styles.activatePseudoClass(node, pcSecond, pcExcluded);
        assertThat(node.getPseudoClassStates()).containsExactly(pcFirst, pcSecond);
    }

    ///////////////////////////////////////////////////////////////////////////

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testAppendStyleNullNode() {
        assertThatNullPointerException().isThrownBy(
            () -> Styles.appendStyle(null, "-fx-background-color", "red")
        );
    }

    @Test
    void testAppendStyleValid() {
        var node = new Region();
        Styles.appendStyle(node, "-fx-background-color", "red");
        assertThat(node.getStyle()).isEqualTo("-fx-background-color:red;");
    }

    @Test
    void testAppendStyleEmptyProperty() {
        var node = new Region();
        Styles.appendStyle(node, "", "red");
        assertThat(node.getStyle()).isEmpty();
    }

    @Test
    void testAppendStyleEmptyValue() {
        var node = new Region();
        Styles.appendStyle(node, "-fx-background-color", "");
        assertThat(node.getStyle()).isEmpty();
    }

    @Test
    void testAppendStyleMultipleProperties() {
        var node = new Region();
        Styles.appendStyle(node, "-fx-background-color", "red");
        Styles.appendStyle(node, "-fx-text-fill", "white");
        assertThat(node.getStyle()).isEqualTo("-fx-background-color:red;-fx-text-fill:white;");
    }

    @Test
    void testAppendStyleDuplicateProperty() {
        var node = new Region();
        Styles.appendStyle(node, "-fx-background-color", "red");
        Styles.appendStyle(node, "-fx-background-color", "blue");
        // that's it, "append" appends, no check for duplicates
        assertThat(node.getStyle()).isEqualTo("-fx-background-color:red;-fx-background-color:blue;");
    }

    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testRemoveStyleValidProperty() {
        var node = new Region();
        node.setStyle("-fx-background-color:red;-fx-text-fill:white;");
        Styles.removeStyle(node, "-fx-background-color");
        assertThat(node.getStyle())
            .contains("-fx-text-fill:white;")
            .doesNotContain("-fx-background-color:red;");
    }

    @Test
    void testRemoveStyleNonexistentProperty() {
        var node = new Region();
        node.setStyle("-fx-background-color:red;");
        Styles.removeStyle(node, "-fx-text-fill");
        assertThat(node.getStyle()).contains("-fx-background-color:red;");
    }

    @Test
    void testRemoveStyleEmptyProperty() {
        var node = new Region();
        node.setStyle("-fx-background-color:red;");
        Styles.removeStyle(node, "");
        assertThat(node.getStyle()).contains("-fx-background-color:red;");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testRemoveStyleNullNode() {
        assertThatNullPointerException().isThrownBy(
            () -> Styles.removeStyle(null, "-fx-background-color")
        );
    }

    @Test
    void testRemoveStyleFromEmptyNode() {
        var node = new Region();
        Styles.removeStyle(node, "-fx-background-color");
        assertThat(node.getStyle()).isEmpty();
    }

    @Test
    void testRemoveMultipleStyles() {
        var node = new Region();
        node.setStyle("-fx-background-color:red;-fx-text-fill:white;");
        Styles.removeStyle(node, "-fx-background-color");
        Styles.removeStyle(node, "-fx-text-fill");
        assertThat(node.getStyle()).isEmpty();
    }

    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testToDataURIWithValidCSS() {
        String css = "body { font-size: 16px; }";

        String dataUri = Styles.toDataURI(css);
        byte[] decodedBytes = Base64.getDecoder().decode(dataUri.substring(dataUri.indexOf(",") + 1));

        assertThat(dataUri).startsWith(Styles.DATA_URI_PREFIX);
        assertThat(new String(decodedBytes)).isEqualTo(css);
    }

    @Test
    void testToDataURIWithEmptyCSS() {
        String css = "";

        String dataUri = Styles.toDataURI(css);
        byte[] decodedBytes = Base64.getDecoder().decode(dataUri.substring(dataUri.indexOf(",") + 1));

        assertThat(dataUri).startsWith(Styles.DATA_URI_PREFIX);
        assertThat(new String(decodedBytes)).isEmpty();
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testToDataURIWithNullCSS() {
        assertThatNullPointerException().isThrownBy(
            () -> Styles.toDataURI(null)
        );
    }

    @Test
    void testToDataURIWithWhitespaceCSS() {
        String css = "   ";

        String dataUri = Styles.toDataURI(css);
        byte[] decodedBytes = Base64.getDecoder().decode(dataUri.substring(dataUri.indexOf(",") + 1));

        assertThat(dataUri).startsWith(Styles.DATA_URI_PREFIX);
        assertThat(new String(decodedBytes)).isEqualTo(css);
    }

    @Test
    void testToDataURIWithSpecialCharactersCSS() {
        String css = "#id { background-image: url('https://example.com/bg.png'); }";

        String dataUri = Styles.toDataURI(css);
        byte[] decodedBytes = Base64.getDecoder().decode(dataUri.substring(dataUri.indexOf(",") + 1));

        assertThat(dataUri).startsWith(Styles.DATA_URI_PREFIX);
        assertThat(new String(decodedBytes)).isEqualTo(css);
    }
}
