/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase;

import atlantafx.sampler.page.AbstractPage;

public class TextEditor extends AbstractPage {

    public static final String NAME = "Text Editor";

    public TextEditor() {
        super();
        createView();
    }

    private void createView() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
