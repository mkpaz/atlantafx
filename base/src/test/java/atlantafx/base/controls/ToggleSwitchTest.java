/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.geometry.HorizontalDirection;
import javafx.scene.control.ToggleGroup;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class ToggleSwitchTest {

    private ToggleSwitch toggleSwitch;

    @BeforeEach
    public void setUp() {
        toggleSwitch = new ToggleSwitch();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(toggleSwitch.getStyleClass()).contains("toggle-switch");
    }

    @Test
    public void testDefaultSelectedIsFalse() {
        assertThat(toggleSwitch.isSelected()).isFalse();
    }

    @Test
    public void testSetSelected() {
        toggleSwitch.setSelected(true);
        assertThat(toggleSwitch.isSelected()).isTrue();

        toggleSwitch.setSelected(false);
        assertThat(toggleSwitch.isSelected()).isFalse();
    }

    @Test
    public void testSelectedProperty() {
        assertThat(toggleSwitch.selectedProperty().get()).isFalse();

        toggleSwitch.selectedProperty().set(true);
        assertThat(toggleSwitch.isSelected()).isTrue();
    }

    @Test
    public void testFireTogglesSelected() {
        assertThat(toggleSwitch.isSelected()).isFalse();

        toggleSwitch.fire();
        assertThat(toggleSwitch.isSelected()).isTrue();

        toggleSwitch.fire();
        assertThat(toggleSwitch.isSelected()).isFalse();
    }

    @Test
    public void testFireDoesNotToggleWhenDisabled() {
        toggleSwitch.setDisable(true);
        toggleSwitch.fire();
        assertThat(toggleSwitch.isSelected()).isFalse();
    }

    @Test
    public void testDefaultToggleGroupIsNull() {
        assertThat(toggleSwitch.getToggleGroup()).isNull();
    }

    @Test
    public void testSetToggleGroup() {
        var group = new ToggleGroup();
        toggleSwitch.setToggleGroup(group);
        assertThat(toggleSwitch.getToggleGroup()).isSameAs(group);
        assertThat(group.getToggles()).contains(toggleSwitch);
    }

    @Test
    public void testToggleGroupRemovesFromOldGroup() {
        var group1 = new ToggleGroup();
        var group2 = new ToggleGroup();

        toggleSwitch.setToggleGroup(group1);
        assertThat(group1.getToggles()).contains(toggleSwitch);

        toggleSwitch.setToggleGroup(group2);
        assertThat(group1.getToggles()).doesNotContain(toggleSwitch);
        assertThat(group2.getToggles()).contains(toggleSwitch);
    }

    @Test
    public void testSetToggleGroupToNullRemovesFromGroup() {
        var group = new ToggleGroup();
        toggleSwitch.setToggleGroup(group);
        assertThat(group.getToggles()).contains(toggleSwitch);

        toggleSwitch.setToggleGroup(null);
        assertThat(toggleSwitch.getToggleGroup()).isNull();
        assertThat(group.getToggles()).doesNotContain(toggleSwitch);
    }

    @Test
    public void testToggleGroupSelectsToggle() {
        var group = new ToggleGroup();
        toggleSwitch.setToggleGroup(group);
        var other = new ToggleSwitch();
        other.setToggleGroup(group);

        toggleSwitch.setSelected(true);
        assertThat(group.getSelectedToggle()).isSameAs(toggleSwitch);

        other.setSelected(true);
        assertThat(group.getSelectedToggle()).isSameAs(other);
        assertThat(toggleSwitch.isSelected()).isFalse();
    }

    @Test
    public void testDefaultLabelPositionIsLeft() {
        assertThat(toggleSwitch.getLabelPosition()).isEqualTo(HorizontalDirection.LEFT);
    }

    @Test
    public void testSetLabelPosition() {
        toggleSwitch.setLabelPosition(HorizontalDirection.RIGHT);
        assertThat(toggleSwitch.getLabelPosition()).isEqualTo(HorizontalDirection.RIGHT);

        toggleSwitch.setLabelPosition(HorizontalDirection.LEFT);
        assertThat(toggleSwitch.getLabelPosition()).isEqualTo(HorizontalDirection.LEFT);
    }

    @Test
    public void testLabelPositionProperty() {
        toggleSwitch.labelPositionProperty().set(HorizontalDirection.RIGHT);
        assertThat(toggleSwitch.getLabelPosition()).isEqualTo(HorizontalDirection.RIGHT);
    }

    @Test
    public void testSelectedPseudoClass() {
        assertThat(toggleSwitch.getPseudoClassStates().stream()
                .anyMatch(pc -> pc.getPseudoClassName().equals("selected"))).isFalse();

        toggleSwitch.setSelected(true);
        assertThat(toggleSwitch.getPseudoClassStates().stream()
                .anyMatch(pc -> pc.getPseudoClassName().equals("selected"))).isTrue();
    }

    @Test
    public void testConstructorWithText() {
        var ts = new ToggleSwitch("Test Label");
        assertThat(ts.getText()).isEqualTo("Test Label");
        assertThat(ts.isSelected()).isFalse();
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = toggleSwitch.createDefaultSkin();
        assertThat(skin).isInstanceOf(ToggleSwitchSkin.class);
    }
}
