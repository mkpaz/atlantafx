/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase;

import atlantafx.sampler.page.AbstractPage;

public class FileManager extends AbstractPage {

    public static final String NAME = "File Manager";

    public FileManager() {
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
