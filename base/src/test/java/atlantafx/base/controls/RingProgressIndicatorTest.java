/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class RingProgressIndicatorTest {

    private RingProgressIndicator indicator;

    @BeforeEach
    public void setUp() {
        indicator = new RingProgressIndicator();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(indicator.getStyleClass()).contains("progress-indicator");
    }

    @Test
    public void testStyleClassAfterSkinInstallation() {
        // The skin adds the "ring-progress-indicator" style class
        indicator.createDefaultSkin();
        assertThat(indicator.getStyleClass()).contains("ring-progress-indicator");
    }

    @Test
    public void testDefaultProgressIsNegativeOne() {
        assertThat(indicator.getProgress()).isEqualTo(-1.0);
    }

    @Test
    public void testConstructorWithProgress() {
        var ind = new RingProgressIndicator(0.5);
        assertThat(ind.getProgress()).isEqualTo(0.5);
        assertThat(ind.isReverse()).isFalse();
    }

    @Test
    public void testConstructorWithProgressAndReverse() {
        var ind = new RingProgressIndicator(0.75, true);
        assertThat(ind.getProgress()).isEqualTo(0.75);
        assertThat(ind.isReverse()).isTrue();
    }

    @Test
    public void testSetProgress() {
        indicator.setProgress(0.0);
        assertThat(indicator.getProgress()).isEqualTo(0.0);

        indicator.setProgress(0.5);
        assertThat(indicator.getProgress()).isEqualTo(0.5);

        indicator.setProgress(1.0);
        assertThat(indicator.getProgress()).isEqualTo(1.0);
    }

    @Test
    public void testProgressProperty() {
        indicator.setProgress(0.3);
        assertThat(indicator.progressProperty().get()).isEqualTo(0.3);

        indicator.progressProperty().set(0.7);
        assertThat(indicator.getProgress()).isEqualTo(0.7);
    }

    @Test
    public void testDefaultReverseIsFalse() {
        assertThat(indicator.isReverse()).isFalse();
    }

    @Test
    public void testReversePropertyReturnsReadOnlyProperty() {
        var reverseProp = indicator.reverseProperty();
        assertThat(reverseProp.getName()).isEqualTo("reverse");
        assertThat(reverseProp.getBean()).isSameAs(indicator);
    }

    @Test
    public void testDefaultGraphicIsNull() {
        assertThat(indicator.getGraphic()).isNull();
    }

    @Test
    public void testSetGraphic() {
        var node = new Label("test");
        indicator.setGraphic(node);
        assertThat(indicator.getGraphic()).isSameAs(node);

        indicator.setGraphic(null);
        assertThat(indicator.getGraphic()).isNull();
    }

    @Test
    public void testGraphicProperty() {
        var node = new Label("test");
        indicator.graphicProperty().set(node);
        assertThat(indicator.getGraphic()).isSameAs(node);

        assertThat(indicator.graphicProperty().get()).isSameAs(node);
    }

    @Test
    public void testDefaultStringConverterIsNull() {
        assertThat(indicator.getStringConverter()).isNull();
    }

    @Test
    public void testSetStringConverter() {
        var converter = new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return String.format("%.0f%%", value * 100);
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string.replace("%", "")) / 100;
            }
        };

        indicator.setStringConverter(converter);
        assertThat(indicator.getStringConverter()).isSameAs(converter);

        indicator.setStringConverter(null);
        assertThat(indicator.getStringConverter()).isNull();
    }

    @Test
    public void testStringConverterProperty() {
        var converter = new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return value.toString();
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        };

        indicator.stringConverterProperty().set(converter);
        assertThat(indicator.getStringConverter()).isSameAs(converter);
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = indicator.createDefaultSkin();
        assertThat(skin).isInstanceOf(RingProgressIndicatorSkin.class);
    }
}
