/* SPDX-License-Identifier: MIT */

package atlantafx.validation.l10n;

import java.util.ListResourceBundle;

public class validation_messages_de extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"err.min", "Feld {1} ({0}) muss mindestens {2} sein"}
        };
    }
}