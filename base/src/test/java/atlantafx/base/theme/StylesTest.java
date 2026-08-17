/* SPDX-License-Identifier: MIT */

package atlantafx.base.theme;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

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

    //*************************************************************************

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

    //*************************************************************************

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

    //*************************************************************************

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

    //*************************************************************************

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

    //*************************************************************************

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

    //*************************************************************************

    @Test
    @DisplayName("should encode text CSS content without Base64")
    void testEncodeText() {
        String css = "/* marker */ .root { -fx-font-size: 14px; -fx-text-fill: #ff0000; }";

        String result = Styles.encode(css);

        assertThat(result)
            .startsWith("data:text/css;charset=utf-8,")
            .endsWith(css)
            .isEqualTo("data:text/css;charset=utf-8,/* marker */ .root { -fx-font-size: 14px; -fx-text-fill: #ff0000; }");
    }

    @Test
    @DisplayName("should encode image binary data as Base64")
    void testEncodeImageBase64() {
        byte[] image = new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        String base64 = Base64.getEncoder().encodeToString(image);

        String result = Styles.encode(image, "image/png");

        assertThat(result)
            .startsWith("data:image/png;base64,")
            .isEqualTo("data:image/png;base64," + base64);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("should encode Base64 with null or blank MIME type")
    void testEncodeNullOrBlankMimeType(String mimeType) {
        byte[] bytes = "Hello World".getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.getEncoder().encodeToString(bytes);

        String resultNull = Styles.encode(bytes, null);
        assertThat(resultNull).isEqualTo("data:base64," + base64);

        String resultBlank = Styles.encode(bytes, mimeType);
        assertThat(resultBlank).isEqualTo("data:base64," + base64);
    }

    @Test
    @DisplayName("should encode non-CSS string content as Base64")
    void testEncodeCustomStringAsBase64() {
        String svg = "<svg><rect/></svg>";

        String result = Styles.encode(svg, "image/svg+xml");

        String base64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        assertThat(result).isEqualTo("data:image/svg+xml;base64," + base64);
    }

    @Test
    @DisplayName("should throw on null encoding arguments")
    @SuppressWarnings("all")
    void testThrowExceptionOnNullArguments() {
        assertThatThrownBy(() -> Styles.encode(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Content cannot be null");

        assertThatThrownBy(() -> Styles.encode((byte[]) null, "image/png"))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Bytes cannot be null");
    }

    @Test
    @DisplayName("should decode text to raw bytes")
    void testDecodeTextToBytes() {
        String css = "/* marker */ .root { -fx-font-size: 14px; }";
        String dataUri = Styles.encode(css);

        byte[] bytes = Styles.decode(dataUri);
        String restored = new String(bytes, StandardCharsets.UTF_8);

        assertThat(restored).isEqualTo(css);
    }

    @Test
    @DisplayName("should decode image to raw bytes")
    void testDecodeImageToBytes() {
        byte[] bytes = new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        String dataUri = Styles.encode(bytes, "image/png");

        byte[] restored = Styles.decode(dataUri);
        assertThat(restored).isEqualTo(bytes);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("should decode Base64 without MIME type to raw bytes")
    void testDecodeBase64WithoutMimeTypeToBytes(String blankMime) {
        byte[] bytes = new byte[] {1, 2, 3, 4, 5, 42};
        String dataUri = Styles.encode(bytes, blankMime);

        byte[] restored = Styles.decode(dataUri);
        assertThat(restored).isEqualTo(bytes);
    }

    @Test
    @DisplayName("should be symmetric for encoding and decoding operations")
    void testSymmetryForEncodeAndDecode() {
        String css = "body { color: red; }";
        byte[] bytes = new byte[] {10, 20, 30};

        assertThat(new String(Styles.decode(Styles.encode(css)), StandardCharsets.UTF_8)).isEqualTo(css);
        assertThat(Styles.decode(Styles.encode(bytes, "image/png"))).isEqualTo(bytes);
        assertThat(Styles.decode(Styles.encode(bytes, null))).isEqualTo(bytes);
    }

    @Test
    @DisplayName("should throw on invalid Data URI format")
    void testThrowExceptionOnInvalidDataUri() {
        assertThatThrownBy(() -> Styles.decode("http://localhost"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must start with 'data:'");

        assertThatThrownBy(() -> Styles.decode("data:text/css"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("missing comma separator");
    }

    @Test
    @DisplayName("should decode Base64 URI with omitted mime type")
    void testDecodeOmittedMimeType() {
        byte[] bytes = new byte[] {1, 2, 3, 4, 5};
        String dataUri = "data:base64," + Base64.getEncoder().encodeToString(bytes);

        byte[] restored = Styles.decode(dataUri);
        assertThat(restored).isEqualTo(bytes);
    }

    @Nested
    class NodeDataUriTest {

        @BeforeAll
        static void init() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
            } catch (IllegalStateException e) {
                latch.countDown();
            }
            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        }

        @Test
        @DisplayName("should apply data URI directly to Node")
        void testApplyDataUri() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    var label = new Label("Test Label");
                    var root = new StackPane(label);
                    var scene = new Scene(root);

                    // plain text
                    var cssPlain = ".label { -fx-font-size: 20px; }";
                    var dataUriPlain = "data:text/css;charset=utf-8," + cssPlain;
                    scene.getStylesheets().add(dataUriPlain);

                    assertThat(scene.getStylesheets()).contains(dataUriPlain);

                    // base64 encoded CSS
                    String cssBase64 = ".root { -fx-padding: 10px; }";
                    String dataUriBase64 = "data:text/css;base64,"
                        + Base64.getEncoder().encodeToString(cssBase64.getBytes(StandardCharsets.UTF_8));
                    scene.getStylesheets().add(dataUriBase64);

                    assertThat(scene.getStylesheets()).contains(dataUriBase64);

                    // base64 image data URI
                    byte[] bytes = new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
                    String dataUriBytes = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

                    label.setStyle("-fx-background-image: url(\"" + dataUriBytes + "\");");

                    assertThat(label.getStyle()).contains(dataUriBytes);

                } finally {
                    latch.countDown();
                }
            });

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        }
    }
}
