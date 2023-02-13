/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import static atlantafx.sampler.util.JColorUtils.toHexWithAlpha;

import java.util.Objects;
import javafx.scene.control.SplitPane;
import javafx.scene.paint.Color;

final class PlayerScreen extends SplitPane {

    private final Model model;

    public PlayerScreen(Model model) {
        super();

        this.model = model;

        createView();
    }

    private void createView() {
        var player = new PlayerPane(model);
        var playlist = new PlaylistPane(model);

        getStyleClass().add("player-screen");
        getItems().setAll(player, playlist);

        model.backgroundColorProperty().addListener((obs, old, val) -> {
            var domColor = Objects.equals(Color.TRANSPARENT, val)
                ? Color.TRANSPARENT
                : Color.color(val.getRed(), val.getGreen(), val.getBlue(), 1);
            var domColor20 = Objects.equals(Color.TRANSPARENT, val)
                ? Color.TRANSPARENT
                : Color.color(val.getRed(), val.getGreen(), val.getBlue(), 0.2);
            var domColor50 = Objects.equals(Color.TRANSPARENT, val)
                ? Color.TRANSPARENT
                : Color.color(val.getRed(), val.getGreen(), val.getBlue(), 0.5);
            var domColor70 = Objects.equals(Color.TRANSPARENT, val)
                ? Color.TRANSPARENT
                : Color.color(val.getRed(), val.getGreen(), val.getBlue(), 0.7);
            setStyle("-color-dominant:" + toHexWithAlpha(domColor) + ";"
                + "-color-dominant-20:" + toHexWithAlpha(domColor20) + ";"
                + "-color-dominant-50:" + toHexWithAlpha(domColor50) + ";"
                + "-color-dominant-70:" + toHexWithAlpha(domColor70) + ";"
                + "-color-dominant-border:" + toHexWithAlpha(domColor70) + ";"
            );
        });
    }
}
