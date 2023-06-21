/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_ALBUM;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_ARTIST;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_TITLE;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.copyImage;

import atlantafx.sampler.Resources;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

@SuppressWarnings("StringOperationCanBeSimplified")
record MediaFile(Path path) {

    private static final Map<String, Metadata> METADATA_CACHE = new HashMap<>();

    // Sadly JavaFX Media API is not user-friendly. If you want to obtain any
    // media file metadata you have to load it to media player instance, which
    // is costly and that instance is not even reusable.
    public void readMetadata(Consumer<Metadata> callback) {
        var media = new Media(path.toUri().toString());
        var mediaPlayer = new MediaPlayer(media);

        // The media information is obtained asynchronously and so not necessarily
        // available immediately after instantiation of the class. All information
        // should however be available if the instance has been associated with a
        // MediaPlayer and that player has transitioned to Status.READY status.
        mediaPlayer.setOnReady(() -> {
            Map<String, Object> metadata = media.getMetadata();
            callback.accept(METADATA_CACHE.computeIfAbsent(path.toAbsolutePath().toString(), k -> {
                var image = getTag(metadata, "image", Image.class, null);
                // clone everything to make sure media player will be garbage collected
                return new Metadata(
                    new String(getTag(metadata, "title", String.class, NO_TITLE)),
                    image != null ? copyImage(image) : null,
                    new String(getTag(metadata, "artist", String.class, NO_ARTIST)),
                    new String(getTag(metadata, "album", String.class, NO_ALBUM)),
                    media.getDuration().toMillis()
                );
            }));

            mediaPlayer.dispose();
        });
    }

    public Media createMedia() {
        return new Media(path.toUri().toString());
    }

    private <T> T getTag(Map<String, Object> metadata, String key, Class<T> type, T defaultValue) {
        Object tag = metadata.get(key);
        return type.isInstance(tag) ? type.cast(tag) : defaultValue;
    }

    ///////////////////////////////////////////////////////////////////////////

    record Metadata(String title, Image image, String artist, String album, double duration) {

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
