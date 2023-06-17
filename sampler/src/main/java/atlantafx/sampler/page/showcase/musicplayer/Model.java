/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import atlantafx.sampler.Launcher;
import atlantafx.sampler.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

final class Model {

    private static final List<Path> DEMO_FILES = getDemoFiles();

    private static List<Path> getDemoFiles() {
        // for the runtime image as it won't create zipfs automatically
        URL media = Launcher.class.getResource(Resources.MODULE_DIR + "media");
        if (media != null && media.toString().startsWith("jar")) {
            try {
                try (var fs = FileSystems.newFileSystem(
                    media.toURI(),
                    Map.of("create", "true")
                )) {
                    return List.of(
                        fs.getPath(Resources.MODULE_DIR + "media/Beat Thee.mp3"),
                        fs.getPath(Resources.MODULE_DIR + "media/Study and Relax.mp3")
                    );
                }
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return List.of(
            Paths.get(Resources.getResource("media/Beat Thee.mp3")),
            Paths.get(Resources.getResource("media/Study and Relax.mp3"))
        );
    }

    private final ObservableList<MediaFile> playlist = FXCollections.observableArrayList();
    private final ReadOnlyBooleanWrapper canGoBack = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper canGoForward = new ReadOnlyBooleanWrapper();
    private final ReadOnlyObjectWrapper<MediaFile> currentTrack = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Color> backgroundColor = new ReadOnlyObjectWrapper<>(Color.TRANSPARENT);

    public Model() {
        canGoBack.bind(Bindings.createBooleanBinding(
            () -> playlist.size() > 1 && getPlaylistPosition() > 0, currentTrack)
        );
        canGoForward.bind(Bindings.createBooleanBinding(
            () -> playlist.size() > 0 && getPlaylistPosition() < playlist.size() - 1, currentTrack));
    }

    private int getPlaylistPosition() {
        if (currentTrack.get() == null) {
            return -1;
        }
        return playlist.indexOf(currentTrack.get());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    public ObservableList<MediaFile> playlist() {
        return playlist;
    }

    public ReadOnlyBooleanProperty canGoBackProperty() {
        return canGoBack.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty canGoForwardProperty() {
        return canGoForward.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<MediaFile> currentTrackProperty() {
        return currentTrack.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor.getReadOnlyProperty();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void play(MediaFile mediaFile) {
        currentTrack.set(Objects.requireNonNull(mediaFile));
    }

    public void playPrevious() {
        if (canGoBack.get()) {
            currentTrack.set(playlist.get(getPlaylistPosition() - 1));
        }
    }

    public void playNext() {
        if (canGoForward.get()) {
            currentTrack.set(playlist.get(getPlaylistPosition() + 1));
        }
    }

    public void reset() {
        currentTrack.set(null);
        setBackgroundColor(null);
    }

    public void setBackgroundColor(Color color) {
        backgroundColor.set(Objects.requireNonNullElse(color, Color.TRANSPARENT));
    }

    public void shuffle() {
        FXCollections.shuffle(playlist);
    }

    public void addFile(MediaFile mediaFile) {
        playlist.add(Objects.requireNonNull(mediaFile));
    }

    public void removeFile(MediaFile mediaFile) {
        playlist.remove(Objects.requireNonNull(mediaFile));
    }

    public void removeAll() {
        reset();
        playlist().clear();
    }

    public void playDemo() {
        DEMO_FILES.forEach(f -> addFile(new MediaFile(f)));
        play(new MediaFile(DEMO_FILES.get(0)));
    }
}
