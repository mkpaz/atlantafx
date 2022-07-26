/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.musicplayer;

import atlantafx.base.controls.Popover;
import atlantafx.base.controls.Spacer;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.controls.Popover.ArrowLocation;
import static atlantafx.base.theme.Styles.*;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.*;
import static atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage.BACKGROUND_OPACITY;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.formatDuration;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.getDominantColor;
import static javafx.geometry.Orientation.VERTICAL;
import static javafx.geometry.Pos.CENTER;
import static org.kordamp.ikonli.material2.Material2AL.CLEAR_ALL;
import static org.kordamp.ikonli.material2.Material2AL.EQUALS;
import static org.kordamp.ikonli.material2.Material2MZ.*;
import static org.kordamp.ikonli.material2.Material2OutlinedAL.FAST_FORWARD;
import static org.kordamp.ikonli.material2.Material2OutlinedAL.FAST_REWIND;

final class Player extends VBox {

    private static final int PANEL_MAX_WIDTH = 220;

    private final ObjectProperty<MediaPlayer> currentPlayer = new SimpleObjectProperty<>();

    public Player(Model model) {
        Rectangle coverImage = new Rectangle(0, 0, 150, 150);
        coverImage.setArcWidth(20.0);
        coverImage.setArcHeight(20.0);
        coverImage.setFill(new ImagePattern(NO_IMAGE));

        var trackTitle = new Label(NO_TITLE);
        trackTitle.setAlignment(CENTER);
        trackTitle.setMaxWidth(Double.MAX_VALUE);
        trackTitle.getStyleClass().add(TITLE_3);

        var trackArtist = new Label(NO_ARTIST);
        trackArtist.setAlignment(CENTER);
        trackArtist.setMaxWidth(Double.MAX_VALUE);

        var trackAlbum = new Label(NO_ALBUM);
        trackAlbum.setAlignment(CENTER);
        trackAlbum.setMaxWidth(Double.MAX_VALUE);

        // == Media controls ==

        var prevBtn = new Button("", new FontIcon(FAST_REWIND));
        prevBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        prevBtn.setShape(new Circle(50));
        prevBtn.setTooltip(new Tooltip("Previous"));
        prevBtn.disableProperty().bind(model.canGoBackProperty().not());
        prevBtn.setOnAction(e -> model.playPrevious());

        var playIcon = new FontIcon(PLAY_ARROW);
        playIcon.setIconSize(32);

        var playBtn = new Button("", playIcon);
        playBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        playBtn.setShape(new Circle(50));

        var nextBtn = new Button("", new FontIcon(FAST_FORWARD));
        nextBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        nextBtn.setShape(new Circle(50));
        nextBtn.disableProperty().bind(model.canGoForwardProperty().not());
        nextBtn.setOnAction(e -> model.playNext());
        nextBtn.setTooltip(new Tooltip("Next"));

        var mediaControls = new HBox(20);
        mediaControls.getChildren().setAll(prevBtn, playBtn, nextBtn);
        mediaControls.setAlignment(CENTER);

        // == Time controls ==

        var timeSlider = new Slider(0, 1, 0);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.setMinWidth(PANEL_MAX_WIDTH);
        timeSlider.setMaxWidth(PANEL_MAX_WIDTH);

        var currentTimeLabel = new Label("0.0");
        currentTimeLabel.getStyleClass().add(TEXT_SMALL);

        var endTimeLabel = new Label("5.0");
        endTimeLabel.getStyleClass().add(TEXT_SMALL);

        var timeMarkersBox = new HBox(5);
        timeMarkersBox.getChildren().setAll(currentTimeLabel, new Spacer(), endTimeLabel);
        timeMarkersBox.setMaxWidth(PANEL_MAX_WIDTH);

        // == Extra controls ==

        var clearPlaylistBtn = new Button("", new FontIcon(CLEAR_ALL));
        clearPlaylistBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        clearPlaylistBtn.setShape(new Circle(50));
        clearPlaylistBtn.setTooltip(new Tooltip("Clear"));
        clearPlaylistBtn.setOnAction(e -> model.removeAll());

        var shuffleBtn = new Button("", new FontIcon(SHUFFLE));
        shuffleBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        shuffleBtn.setShape(new Circle(50));
        shuffleBtn.setTooltip(new Tooltip("Shuffle"));
        shuffleBtn.setOnAction(e -> model.shuffle());

        var volumeSlider = new Slider(0, 1, 0.75);
        volumeSlider.setOrientation(VERTICAL);

        var volumeBar = new VBox(5);
        volumeBar.getChildren().setAll(new FontIcon(VOLUME_UP), volumeSlider, new FontIcon(VOLUME_OFF));
        volumeBar.setAlignment(CENTER);

        var volumePopover = new Popover(volumeBar);
        volumePopover.setHeaderAlwaysVisible(false);
        volumePopover.setArrowLocation(ArrowLocation.TOP_LEFT);

        var volumeBtn = new Button("", new FontIcon(VOLUME_UP));
        volumeBtn.getStyleClass().addAll(BUTTON_CIRCLE);
        volumeBtn.setShape(new Circle(50));
        volumeBtn.setOnAction(e -> volumePopover.show(volumeBtn));

        var extraControls = new HBox(10);
        extraControls.getChildren().setAll(clearPlaylistBtn, shuffleBtn, new Spacer(), volumeBtn);
        extraControls.setMaxWidth(PANEL_MAX_WIDTH);

        // == Root ==

        setSpacing(5);
        getStyleClass().add("player");
        setAlignment(CENTER);
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

        // == Play ==

        backgroundProperty().bind(Bindings.createObjectBinding(() -> {
            Color color = model.backgroundColorProperty().get();
            return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
        }, model.backgroundColorProperty()));

        playBtn.setOnAction(e -> {
            MediaPlayer player = currentPlayer.get();
            if (player == null) { return; }
            switch (player.getStatus()) {
                case READY, PAUSED, STOPPED -> player.play();
                case PLAYING -> player.pause();
                default -> { }
            }
        });

        InvalidationListener timeChangeListener = obs -> {
            if (currentPlayer.get() == null) { return; }

            var duration = currentPlayer.get().getCurrentTime();
            var seconds = duration != null && !duration.equals(Duration.ZERO) ? duration.toSeconds() : 0;

            if (!timeSlider.isValueChanging()) { timeSlider.setValue(seconds); }
            currentTimeLabel.setText(seconds > 0 ? formatDuration(duration) : "0.0");
        };

        InvalidationListener sliderChangeListener = obs -> {
            if (currentPlayer.get() == null) { return; }
            long max = (long) currentPlayer.get().getMedia().getDuration().toSeconds();
            long sliderVal = (long) timeSlider.getValue();
            if (sliderVal <= max && timeSlider.isValueChanging()) {
                currentPlayer.get().seek(Duration.seconds(sliderVal));
            }
        };
        timeSlider.valueProperty().addListener(sliderChangeListener);

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
                model.setBackgroundColor(image != NO_IMAGE ? getDominantColor(image, BACKGROUND_OPACITY) : null);

                trackTitle.setText(getTag(media, "title", String.class, NO_TITLE));
                trackArtist.setText(getTag(media, "artist", String.class, NO_ARTIST));
                trackAlbum.setText(getTag(media, "album", String.class, NO_ALBUM));

                timeSlider.setMax(media.getDuration().toSeconds());
                endTimeLabel.setText(formatDuration(media.getDuration()));

                playIcon.iconCodeProperty().bind(Bindings.createObjectBinding(() -> {
                    if (mediaPlayer.statusProperty().get() == null) { return EQUALS; }
                    return switch (mediaPlayer.getStatus()) {
                        case READY, PAUSED, STOPPED -> PLAY_ARROW;
                        case PLAYING -> PAUSE;
                        default -> EQUALS;
                    };
                }, mediaPlayer.statusProperty()));

                mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
                mediaPlayer.currentTimeProperty().addListener(timeChangeListener);
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
                old.currentTimeProperty().removeListener(timeChangeListener);
                playIcon.iconCodeProperty().unbind();
                old.dispose();
            }
        });
    }

    private <T> T getTag(Media media, String key, Class<T> type, T defaultValue) {
        if (media == null || key == null || type == null) { return defaultValue; }
        Object tag = media.getMetadata().get(key);
        return type.isInstance(tag) ? type.cast(tag) : defaultValue;
    }
}