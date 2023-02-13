/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import static atlantafx.sampler.util.Controls.hyperlink;

import atlantafx.sampler.page.showcase.ShowcasePage;
import java.net.URI;
import java.util.Objects;
import java.util.Set;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class MusicPlayerPage extends ShowcasePage {

    public static final String NAME = "Music Player";
    public static final double BACKGROUND_OPACITY = 0.2;
    public static final Set<String> SUPPORTED_MEDIA_TYPES = Set.of("mp3");
    private static final String STYLESHEET_URL = Objects.requireNonNull(
        MusicPlayerPage.class.getResource("music-player.css")).toExternalForm();

    @Override
    public String getName() {
        return NAME;
    }

    private final Model model = new Model();

    public MusicPlayerPage() {
        super();
        createView();
    }

    private void createView() {
        var aboutBox = new HBox(new Text("Simple music player that able to play MP3 files."));
        aboutBox.setPadding(new Insets(5, 0, 5, 0));
        aboutBox.getStyleClass().add("about");

        var creditsBox = new HBox(5,
            new Text("Inspired by ©"),
            hyperlink("Amberol", URI.create("https://gitlab.gnome.org/World/amberol"))
        );
        creditsBox.getStyleClass().add("credits");
        creditsBox.setAlignment(Pos.CENTER_RIGHT);
        creditsBox.setPadding(new Insets(5, 0, 5, 0));

        // ~
        var startScreen = new StartScreen(model);
        var playerScreen = new PlayerScreen(model);

        var root = new BorderPane();
        root.setCenter(startScreen);
        root.setTop(aboutBox);
        root.setBottom(creditsBox);

        root.getStylesheets().add(STYLESHEET_URL);
        root.getStyleClass().add("music-player-showcase");

        model.playlist().addListener((ListChangeListener<MediaFile>) c -> {
            if (model.playlist().size() > 0) {
                root.setCenter(playerScreen);
            } else {
                root.setCenter(startScreen);
            }
        });

        showcase.getChildren().setAll(root);
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
    }
}
