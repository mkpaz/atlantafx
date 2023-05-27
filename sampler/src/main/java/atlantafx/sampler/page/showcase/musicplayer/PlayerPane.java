/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import static atlantafx.base.controls.Popover.ArrowLocation;
import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;
import static atlantafx.base.theme.Styles.SMALL;
import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static atlantafx.base.theme.Styles.TITLE_3;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_ALBUM;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_ARTIST;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_IMAGE;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_TITLE;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.formatDuration;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.getDominantColor;
import static java.lang.Double.MAX_VALUE;
import static javafx.geometry.Orientation.VERTICAL;
import static javafx.geometry.Pos.CENTER;
import static org.kordamp.ikonli.material2.Material2AL.CLEAR_ALL;
import static org.kordamp.ikonli.material2.Material2AL.EQUALS;
import static org.kordamp.ikonli.material2.Material2MZ.PAUSE;
import static org.kordamp.ikonli.material2.Material2MZ.PLAY_ARROW;
import static org.kordamp.ikonli.material2.Material2MZ.SHUFFLE;
import static org.kordamp.ikonli.material2.Material2MZ.VOLUME_OFF;
import static org.kordamp.ikonli.material2.Material2MZ.VOLUME_UP;
import static org.kordamp.ikonli.material2.Material2OutlinedAL.FAST_FORWARD;
import static org.kordamp.ikonli.material2.Material2OutlinedAL.FAST_REWIND;

import atlantafx.base.controls.Popover;
import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.controls.Spacer;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

final class PlayerPane extends VBox {

    private static final int PANEL_MAX_WIDTH = 220;

    private final Model model;
    private final ObjectProperty<MediaPlayer> currentPlayer = new SimpleObjectProperty<>();

    private Rectangle coverImage;
    private Label trackTitle;
    private Label trackArtist;
    private Label trackAlbum;

    private FontIcon playIcon;
    private Button playBtn;

    private Slider timeSlider;
    private Slider volumeSlider;
    private Label currentTimeLabel;
    private Label endTimeLabel;

    public PlayerPane(Model model) {
        super();

        this.model = model;

        createView();
        init();
    }

    private void createView() {
        coverImage = new Rectangle(0, 0, 150, 150);
        coverImage.setArcWidth(20.0);
        coverImage.setArcHeight(20.0);
        coverImage.setFill(new ImagePattern(NO_IMAGE));

        trackTitle = new Label(NO_TITLE);
        trackTitle.getStyleClass().add(TITLE_3);
        trackTitle.setAlignment(CENTER);
        trackTitle.setMaxWidth(MAX_VALUE);

        trackArtist = new Label(NO_ARTIST);
        trackArtist.setAlignment(CENTER);
        trackArtist.setMaxWidth(MAX_VALUE);

        trackAlbum = new Label(NO_ALBUM);
        trackAlbum.setAlignment(CENTER);
        trackAlbum.setMaxWidth(MAX_VALUE);

        // == MEDIA CONTROLS ==

        var prevBtn = new Button(null, new FontIcon(FAST_REWIND));
        prevBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        prevBtn.setTooltip(new Tooltip("Previous"));
        prevBtn.disableProperty().bind(model.canGoBackProperty().not());
        prevBtn.setOnAction(e -> model.playPrevious());

        playIcon = new FontIcon(PLAY_ARROW);

        playBtn = new Button(null, playIcon);
        playBtn.getStyleClass().addAll("play", BUTTON_CIRCLE);

        var nextBtn = new Button(null, new FontIcon(FAST_FORWARD));
        nextBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        nextBtn.disableProperty().bind(model.canGoForwardProperty().not());
        nextBtn.setOnAction(e -> model.playNext());
        nextBtn.setTooltip(new Tooltip("Next"));

        var mediaControls = new HBox(20);
        mediaControls.getStyleClass().add("media-controls");
        mediaControls.getChildren().setAll(prevBtn, playBtn, nextBtn);
        mediaControls.setAlignment(CENTER);

        // == TIME CONTROLS ==

        timeSlider = new Slider(0, 1, 0);
        timeSlider.setSkin(new ProgressSliderSkin(timeSlider));
        timeSlider.getStyleClass().add("time-slider");
        timeSlider.setMinWidth(PANEL_MAX_WIDTH);
        timeSlider.setMaxWidth(PANEL_MAX_WIDTH);

        currentTimeLabel = new Label("0.0");
        currentTimeLabel.getStyleClass().add(TEXT_SMALL);

        endTimeLabel = new Label("5.0");
        endTimeLabel.getStyleClass().add(TEXT_SMALL);

        var timeMarkersBox = new HBox(5);
        timeMarkersBox.getChildren().setAll(currentTimeLabel, new Spacer(), endTimeLabel);
        timeMarkersBox.setMaxWidth(PANEL_MAX_WIDTH);

        // == EXTRA CONTROLS ==

        var clearPlaylistBtn = new Button(null, new FontIcon(CLEAR_ALL));
        clearPlaylistBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        clearPlaylistBtn.setTooltip(new Tooltip("Clear"));
        clearPlaylistBtn.setOnAction(e -> model.removeAll());

        var shuffleBtn = new Button(null, new FontIcon(SHUFFLE));
        shuffleBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        shuffleBtn.setTooltip(new Tooltip("Shuffle"));
        shuffleBtn.setOnAction(e -> model.shuffle());

        volumeSlider = new Slider(0, 1, 0.75);
        volumeSlider.setSkin(new ProgressSliderSkin(volumeSlider));
        volumeSlider.getStyleClass().add(SMALL);
        volumeSlider.setOrientation(VERTICAL);

        var volumeBar = new VBox(5);
        volumeBar.getChildren().setAll(new FontIcon(VOLUME_UP), volumeSlider, new FontIcon(VOLUME_OFF));
        volumeBar.setAlignment(CENTER);

        var volumePopover = new Popover(volumeBar);
        volumePopover.setHeaderAlwaysVisible(false);
        volumePopover.setArrowLocation(ArrowLocation.TOP_LEFT);

        var volumeBtn = new Button(null, new FontIcon(VOLUME_UP));
        volumeBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        volumeBtn.setOnAction(e -> volumePopover.show(volumeBtn));

        var extraControls = new HBox(10);
        extraControls.getChildren().setAll(clearPlaylistBtn, shuffleBtn, new Spacer(), volumeBtn);
        extraControls.setMaxWidth(PANEL_MAX_WIDTH);

        // ~

        getStyleClass().add("player");
        setAlignment(CENTER);
        setSpacing(5);
        setMinWidth(300);
        getChildren().setAll(
            new Spacer(VERTICAL),
            new StackPane(coverImage),
            new Spacer(10, VERTICAL),
            trackTitle,
            trackArtist,
            trackAlbum,
            new Spacer(20, VERTICAL),
            mediaControls,
            new Spacer(10, VERTICAL),
            timeSlider,
            timeMarkersBox,
            new Spacer(10, VERTICAL),
            extraControls,
            new Spacer(VERTICAL)
        );
    }

    private void init() {
        heightProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            int size = val.intValue() < 600 ? 150 : 250;
            coverImage.setWidth(size);
            coverImage.setHeight(size);
        });

        playBtn.setOnAction(e -> {
            MediaPlayer player = currentPlayer.get();
            if (player == null) {
                return;
            }
            switch (player.getStatus()) {
                case READY, PAUSED, STOPPED -> player.play();
                case PLAYING -> player.pause();
                default -> {
                }
            }
        });

        InvalidationListener mediaTimeChangeListener = obs -> {
            if (currentPlayer.get() == null) {
                return;
            }

            var duration = currentPlayer.get().getCurrentTime();
            var seconds = duration != null && !duration.equals(Duration.ZERO) ? duration.toSeconds() : 0;

            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(seconds);
            }
            currentTimeLabel.setText(seconds > 0 ? formatDuration(duration) : "0.0");
        };

        timeSlider.valueProperty().addListener(obs -> {
            if (currentPlayer.get() == null) {
                return;
            }
            long max = (long) currentPlayer.get().getMedia().getDuration().toSeconds();
            long sliderVal = (long) timeSlider.getValue();
            if (sliderVal <= max && timeSlider.isValueChanging()) {
                currentPlayer.get().seek(Duration.seconds((double) sliderVal));
            }
        });

        model.currentTrackProperty().addListener((obs, old, val) -> {
            if (val == null) {
                coverImage.setFill(new ImagePattern(NO_IMAGE));
                trackTitle.setText(NO_TITLE);
                trackArtist.setText(NO_ARTIST);
                trackAlbum.setText(NO_ALBUM);
                timeSlider.setValue(0);
                currentPlayer.set(null);
                return;
            }

            Media media = val.createMedia();
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                Image image = getTag(media, "image", Image.class, NO_IMAGE);
                coverImage.setFill(new ImagePattern(image));
                model.setBackgroundColor(image != NO_IMAGE ? getDominantColor(image, 1.0) : null);

                trackTitle.setText(getTag(media, "title", String.class, NO_TITLE));
                trackArtist.setText(getTag(media, "artist", String.class, NO_ARTIST));
                trackAlbum.setText(getTag(media, "album", String.class, NO_ALBUM));

                timeSlider.setMax(media.getDuration().toSeconds());
                endTimeLabel.setText(formatDuration(media.getDuration()));

                playIcon.iconCodeProperty().bind(Bindings.createObjectBinding(() -> {
                    if (mediaPlayer.statusProperty().get() == null) {
                        return EQUALS;
                    }
                    return switch (mediaPlayer.getStatus()) {
                        case READY, PAUSED, STOPPED -> PLAY_ARROW;
                        case PLAYING -> PAUSE;
                        default -> EQUALS;
                    };
                }, mediaPlayer.statusProperty()));

                mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
                mediaPlayer.currentTimeProperty().addListener(mediaTimeChangeListener);
            });
            mediaPlayer.setOnEndOfMedia(model::playNext);

            currentPlayer.set(mediaPlayer);
            mediaPlayer.play();
        });

        // remove all listeners and dispose old player
        currentPlayer.addListener((obs, old, val) -> {
            if (old != null) {
                old.stop();
                old.volumeProperty().unbind();
                old.currentTimeProperty().removeListener(mediaTimeChangeListener);
                playIcon.iconCodeProperty().unbind();
                old.dispose();
            }
        });
    }

    private <T> T getTag(Media media, String key, Class<T> type, T defaultValue) {
        if (media == null || key == null || type == null) {
            return defaultValue;
        }
        Object tag = media.getMetadata().get(key);
        return type.isInstance(tag) ? type.cast(tag) : defaultValue;
    }
}
