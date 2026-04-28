/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.scene.control.ToggleGroup;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class ChipTest {

    @Test
    public void testDefaultStyleClass() {
        var chip = new Chip();
        assertThat(chip.getStyleClass()).contains("chip");
    }

    @Test
    public void testTextConstructor() {
        var chip = new Chip("Tag");
        assertThat(chip.getText()).isEqualTo("Tag");
    }

    @Test
    public void testDefaultPropertyValues() {
        var chip = new Chip();
        assertThat(chip.isSelected()).isFalse();
        assertThat(chip.getToggleGroup()).isNull();
        assertThat(chip.getOnClose()).isNull();
    }

    @Test
    public void testSelectedProperty() {
        var chip = new Chip();
        chip.setSelected(true);
        assertThat(chip.isSelected()).isTrue();

        chip.setSelected(false);
        assertThat(chip.isSelected()).isFalse();
    }

    @Test
    public void testToggleGroupProperty() {
        var chip = new Chip();
        var group = new ToggleGroup();
        chip.setToggleGroup(group);
        assertThat(chip.getToggleGroup()).isSameAs(group);
        assertThat(group.getToggles()).contains(chip);
    }

    @Test
    public void testToggleGroupSelection() {
        var group = new ToggleGroup();
        var c1 = new Chip("A");
        var c2 = new Chip("B");
        c1.setToggleGroup(group);
        c2.setToggleGroup(group);

        c1.setSelected(true);
        assertThat(group.getSelectedToggle()).isSameAs(c1);

        c2.setSelected(true);
        assertThat(group.getSelectedToggle()).isSameAs(c2);
        assertThat(c1.isSelected()).isFalse();
    }

    @Test
    public void testOnCloseProperty() {
        var chip = new Chip();
        assertThat(chip.getOnClose()).isNull();

        var handler = new javafx.event.EventHandler<javafx.event.Event>() {
            @Override
            public void handle(javafx.event.Event event) {}
        };
        chip.setOnClose(handler);
        assertThat(chip.getOnClose()).isSameAs(handler);
    }

    @Test
    public void testCreateDefaultSkin() {
        var chip = new Chip("Test");
        var skin = chip.createDefaultSkin();
        assertThat(skin).isInstanceOf(ChipSkin.class);
    }
}
