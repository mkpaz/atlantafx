/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import javafx.scene.Parent;

public interface Page {

    String getName();

    Parent getView();

    void reset();
}
