/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import atlantafx.base.theme.Styles;

/**
 * The default skin for the {@link Tile} control.
 */
public class TileSkin extends TileSkinBase<Tile> {

    public TileSkin(Tile control) {
        super(control);

        control.actionProperty().addListener(actionSlotListener);
        actionSlotListener.changed(control.actionProperty(), null, control.getAction());

        pseudoClassStateChanged(Styles.STATE_INTERACTIVE, control.getActionHandler() != null);
        registerChangeListener(
            control.actionHandlerProperty(),
            o -> pseudoClassStateChanged(Styles.STATE_INTERACTIVE, getSkinnable().getActionHandler() != null)
        );

        container.setOnMouseClicked(e -> {
            if (getSkinnable().getActionHandler() != null) {
                getSkinnable().getActionHandler().run();
            }
        });
    }

    @Override
    public void dispose() {
        getSkinnable().actionProperty().removeListener(actionSlotListener);
        unregisterChangeListeners(getSkinnable().actionHandlerProperty());

        super.dispose();
    }
}
