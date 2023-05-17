/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.event;

import javafx.scene.input.KeyCodeCombination;

public final class HotkeyEvent extends Event {

    private final KeyCodeCombination keys;

    public HotkeyEvent(KeyCodeCombination keys) {
        this.keys = keys;
    }

    public KeyCodeCombination getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return "HotkeyEvent{"
            + "keys=" + keys
            + "} " + super.toString();
    }
}
