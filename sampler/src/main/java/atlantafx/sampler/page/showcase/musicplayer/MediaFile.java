/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.*;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.copyImage;

import atlantafx.sampler.Resources;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("StringOperationCanBeSimplified")
record MediaFile(Path path) {

    private static final Map<String, Metadata> METADATA_CACHE = new HashMap<>();

    // JavaFX holds weak references to media players, so without a strong reference a player can be
    // garbage collected before it becomes ready, and it'll never get into the callback.
    private static final Set<MediaPlayer> LOADING_PLAYERS = new HashSet<>();
    private static final Map<String, List<Consumer<Metadata>>> PENDING_CALLBACKS = new HashMap<>();

    // Sadly JavaFX Media API is not user-friendly. If you want to obtain any
    // media file metadata you have to load it to media player instance, which
    // is costly and that instance is not even reusable.
    public void readMetadata(Consumer<Metadata> callback) {
        String key = path.toAbsolutePath().toString();

        Metadata cached = METADATA_CACHE.get(key);
        if (cached != null) {
            callback.accept(cached);
            return;
        }

        List<Consumer<Metadata>> pending = PENDING_CALLBACKS.get(key);
        if (pending != null) {
            pending.add(callback);
            return;
        }

        var media = new Media(path.toUri().toString());
        var mediaPlayer = new MediaPlayer(media);
        PENDING_CALLBACKS.put(key, new ArrayList<>(List.of(callback)));
        LOADING_PLAYERS.add(mediaPlayer);

        // The media information is obtained asynchronously and so not necessarily
        // available immediately after instantiation of the class. All information
        // should however be available if the instance has been associated with a
        // MediaPlayer and that player has transitioned to Status.READY status.
        mediaPlayer.setOnReady(() -> {
            Map<String, Object> metadata = media.getMetadata();
            var image = getTag(metadata, "image", Image.class, NO_IMAGE);
            // clone everything to make sure media player will be garbage collected
            var result = new Metadata(
                new String(getTag(metadata, "title", String.class, NO_TITLE)),
                copyImage(image),
                new String(getTag(metadata, "artist", String.class, NO_ARTIST)),
                new String(getTag(metadata, "album", String.class, NO_ALBUM)),
                media.getDuration().toMillis()
            );
            METADATA_CACHE.put(key, result);

            LOADING_PLAYERS.remove(mediaPlayer);
            mediaPlayer.dispose();
            PENDING_CALLBACKS.remove(key).forEach(c -> c.accept(result));
        });

        mediaPlayer.setOnError(() -> {
            LOADING_PLAYERS.remove(mediaPlayer);
            mediaPlayer.dispose();
            PENDING_CALLBACKS.remove(key);
        });
    }

    public Media createMedia() {
        return new Media(path.toUri().toString());
    }

    private <T> T getTag(Map<String, Object> metadata, String key, Class<T> type, T defaultValue) {
        Object tag = metadata.get(key);
        return type.isInstance(tag) ? type.cast(tag) : defaultValue;
    }

    //*************************************************************************

    record Metadata(String title, @Nullable Image image, String artist, String album, double duration) {

        static final Image NO_IMAGE = new Image(
            Resources.getResourceAsStream("images/no-image.png"), 150, 150, true, false
        );

        static final Image NO_IMAGE_ALT = new Image(
            Resources.getResourceAsStream("images/papirus/mimetypes/audio-mp3.png"), 150, 150, true, false
        );

        static final String NO_TITLE = "Unknown title";
        static final String NO_ARTIST = "Unknown artist";
        static final String NO_ALBUM = "Unknown album";
    }
}
