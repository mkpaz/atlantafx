/* SPDX-License-Identifier: MIT */

package atlantafx.validation.l10n;

import java.util.ListResourceBundle;

public class format_messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"err.range", "Field {1} with value {0} must be between {2} and {3}"}
        };
    }
}