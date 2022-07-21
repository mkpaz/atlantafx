/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase;

import atlantafx.sampler.page.AbstractPage;

public class Chat extends AbstractPage {

    public static final String NAME = "Chat";

    public Chat() {
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
