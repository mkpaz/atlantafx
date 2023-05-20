/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import atlantafx.sampler.page.showcase.ShowcasePage;
import java.util.Objects;
import java.util.Set;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class MusicPlayerPage extends ShowcasePage {

    public static final String NAME = "Music Player";
    public static final Set<String> SUPPORTED_MEDIA_TYPES = Set.of("mp3");
    private static final String STYLESHEET_URL =
        Objects.requireNonNull(MusicPlayerPage.class.getResource("music-player.css")).toExternalForm();

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
        var startScreen = new StartScreen(model);
        var playerScreen = new PlayerScreen(model);

        var root = new BorderPane();
        root.setId("music-player-showcase");
        root.setCenter(startScreen);
        root.getStylesheets().add(STYLESHEET_URL);

        model.playlist().addListener((ListChangeListener<MediaFile>) c -> {
            if (model.playlist().size() > 0) {
                root.setCenter(playerScreen);
            } else {
                root.setCenter(startScreen);
            }
        });

        setWindowTitle("Music Player", new FontIcon(Feather.MUSIC));
        setAboutInfo("""
            Simple music player that able to play MP3 files. Inspired by ©Amberol."""
        );
        setShowCaseContent(root);
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
    }
}
