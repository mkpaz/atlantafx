/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.musicplayer;

import atlantafx.sampler.Resources;
import atlantafx.sampler.page.showcase.ShowcasePage;
import atlantafx.sampler.util.Containers;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public class MusicPlayerPage extends ShowcasePage {

    public static final String NAME = "Music Player";
    public static final double BACKGROUND_OPACITY = 0.1;
    private static final String STYLESHEET_URL = Objects.requireNonNull(
            MusicPlayerPage.class.getResource("music-player.css")).toExternalForm();
    private static final Image PLUG_IMAGE = new Image(Resources.getResourceAsStream("images/vinyl.jpg"));

    @Override
    public String getName() { return NAME; }

    private final Model model = new Model();

    public MusicPlayerPage() {
        super();
        createView();
    }

    private void createView() {
        var player = new Player(model);
        player.setVisible(false);

        BackgroundImage backgroundImage = new BackgroundImage(
                PLUG_IMAGE,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        var plug = new AnchorPane();
        plug.setBackground(new Background(backgroundImage));
        plug.setOpacity(0.5);
        plug.setMouseTransparent(false);

        var playerStack = new StackPane(player, plug);
        model.playlist().addListener((ListChangeListener<MediaFile>) c -> {
            if (model.playlist().size() > 0) {
                player.setVisible(true);
                plug.setVisible(false);
                player.toFront();
            } else {
                player.setVisible(false);
                plug.setVisible(true);
                plug.toFront();
            }
        });

        var playlist = new Playlist(model);

        var root = new SplitPane();
        root.getStylesheets().add(STYLESHEET_URL);
        root.getStyleClass().add("music-player-showcase");
        root.getItems().setAll(playerStack, playlist);

        showcase.getChildren().setAll(root);
        Containers.setAnchors(root, Insets.EMPTY);
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
    }
}
