/* SPDX-License-Identifier: MIT */

package atlantafx.validation.l10n;

import java.util.ListResourceBundle;

public class validation_messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"err.min", "Field {1} ({0}) must be at least {2}"}
        };
    }
}