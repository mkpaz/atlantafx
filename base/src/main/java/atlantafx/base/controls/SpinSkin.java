/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

/**
 * The default interface for {@link Spin} based loading indicator skins.
 */
public interface SpinSkin {

    /**
     * Sets whether the animation should be started immediately after connecting a skin to a Scene.
     */
    void autostart(boolean autostart);

    /**
     * Starts the animation.
     */
    void start();

    /**
     * Stops the animation.
     */
    void stop();

    /**
     * Returns the maximum skin width, which means the maximum width the skin
     * can have during interpolation.
     */
    double computeMaxWidth(double height);

    /**
     * Returns the maximum skin height, which means the maximum height the skin
     * can have during interpolation.
     */
    double computeMaxHeight(double width);
}
