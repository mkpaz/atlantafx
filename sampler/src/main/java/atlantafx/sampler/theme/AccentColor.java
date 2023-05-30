/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.theme;

import javafx.css.PseudoClass;
import javafx.scene.paint.Color;

public record AccentColor(Color primaryColor, PseudoClass pseudoClass) {

    public static AccentColor primerPurple() {
        return new AccentColor(Color.web("#8250df"), PseudoClass.getPseudoClass("accent-primer-purple"));
    }

    public static AccentColor primerPink() {
        return new AccentColor(Color.web("#bf3989"), PseudoClass.getPseudoClass("accent-primer-pink"));
    }

    public static AccentColor primerCoral() {
        return new AccentColor(Color.web("#c4432b"), PseudoClass.getPseudoClass("accent-primer-coral"));
    }
}
