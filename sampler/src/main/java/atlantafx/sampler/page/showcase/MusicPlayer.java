/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase;

import atlantafx.sampler.page.AbstractPage;

public class MusicPlayer extends AbstractPage {

    public static final String NAME = "Music Player";

    public MusicPlayer() {
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
