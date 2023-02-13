/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import javafx.scene.Parent;

public interface Page {

    int PAGE_HGAP = 30;
    int PAGE_VGAP = 30;

    String getName();

    Parent getView();

    boolean canDisplaySourceCode();

    boolean canChangeThemeSettings();

    void reset();
}
