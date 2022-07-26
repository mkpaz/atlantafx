/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.musicplayer;

import atlantafx.base.controls.Spacer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.sampler.page.showcase.musicplayer.Utils.toWebColor;
import static java.lang.Double.MAX_VALUE;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.stage.FileChooser.ExtensionFilter;
import static org.kordamp.ikonli.material2.Material2AL.ADD;
import static org.kordamp.ikonli.material2.Material2MZ.PLAYLIST_PLAY;

final class Playlist extends VBox {

    public Playlist(Model model) {
        var headerLabel = new Label("Playlist");
        headerLabel.getStyleClass().setAll(TEXT_CAPTION);
        // There's probably some #javafx-bug here. This label uses CSS class that
        // changes font size & weight. When switching between themes it _sometimes_
        // ignores new color variables and remains using old fg color. Like it
        // caches old font or something. The below rule forces it to use proper color.
        headerLabel.setStyle("-fx-text-fill: -color-fg-default;");

        var sizeLabel = new Label("");
        sizeLabel.getStyleClass().add(TEXT_SMALL);

        var sizeDescLabel = new Label("empty");
        sizeDescLabel.getStyleClass().add(TEXT_SMALL);

        var loadProgress = new ProgressBar(1);
        loadProgress.getStyleClass().add(SMALL);
        loadProgress.setMaxWidth(MAX_VALUE);
        loadProgress.setVisible(false);

        var addButton = new Button("Add", new FontIcon(ADD));

        var controlsBox = new HBox();
        controlsBox.getStyleClass().add("controls");
        controlsBox.getChildren().setAll(
                new VBox(5, headerLabel, sizeDescLabel),
                new Spacer(),
                addButton
        );
        controlsBox.setAlignment(CENTER_LEFT);

        var playlist = new ListView<>(model.playlist());
        playlist.setCellFactory(param -> new MediaCell(model));
        playlist.setPlaceholder(new Label("No Content"));

        getStyleClass().add("playlist");
        setSpacing(10);
        getChildren().setAll(controlsBox, loadProgress, playlist);

        // ~

        model.currentTrackProperty().addListener((obs, old, val) -> playlist.refresh());

        model.playlist().addListener((ListChangeListener<MediaFile>) c -> {
            if (model.playlist().size() > 0) {
                sizeLabel.setText(String.valueOf(model.playlist().size()));
                sizeDescLabel.setGraphic(sizeLabel);
                sizeDescLabel.setText("tracks");
            } else {
                sizeDescLabel.setGraphic(null);
                sizeDescLabel.setText("empty");
            }
        });

        model.backgroundColorProperty().addListener((obs, old, val) -> {
            var color = model.backgroundColorProperty().get();
            playlist.setStyle("-color-cell-bg:" + toWebColor(color) + ";");
            setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        });

        addButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("MP3 files (*.mp3)", "*.mp3"));
            List<File> files = fileChooser.showOpenMultipleDialog(getScene().getWindow());
            if (files == null || files.isEmpty()) { return; }

            loadProgress.setVisible(true);
            final Task<Void> task = new Task<>() {
                int progress = 0;

                @Override
                public Void call() throws InterruptedException {
                    for (File file : files) {
                        Thread.sleep(500); // add artificial delay to demonstrate progress bar
                        Platform.runLater(() -> model.addFile(new MediaFile(file)));
                        progress++;
                        updateProgress(progress, files.size());
                    }
                    return null;
                }
            };

            task.setOnSucceeded(te -> loadProgress.setVisible(false));
            loadProgress.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        });
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class MediaCell extends ListCell<MediaFile> {

        private final Model model;
        private final HBox root;
        private final Rectangle coverImage;
        private final Label titleLabel;
        private final Label artistLabel;
        private final FontIcon playMark;

        public MediaCell(Model model) {
            this.model = model;

            coverImage = new Rectangle(0, 0, 32, 32);
            coverImage.setArcWidth(10.0);
            coverImage.setArcHeight(10.0);

            titleLabel = new Label();
            titleLabel.setMaxWidth(MAX_VALUE);
            titleLabel.getStyleClass().add(TEXT_CAPTION);

            artistLabel = new Label();
            artistLabel.setMaxWidth(MAX_VALUE);

            var titleBox = new VBox(5, titleLabel, artistLabel);
            titleBox.setAlignment(CENTER_LEFT);
            HBox.setHgrow(titleBox, ALWAYS);

            playMark = new FontIcon(PLAYLIST_PLAY);

            root = new HBox(10, coverImage, titleBox, playMark);
            root.setAlignment(CENTER_LEFT);
            root.setOnMouseClicked(e -> {
                if (getItem() != null) { model.play(getItem()); }
            });
        }

        @Override
        protected void updateItem(MediaFile mediaFile, boolean empty) {
            super.updateItem(mediaFile, empty);

            if (empty || mediaFile == null) {
                setGraphic(null);
                coverImage.setFill(null);
                titleLabel.setText(null);
                artistLabel.setText(null);
            } else {
                setGraphic(root);

                playMark.setVisible(Objects.equals(mediaFile, model.currentTrackProperty().get()));

                mediaFile.readMetadata(metadata -> {
                    coverImage.setFill(new ImagePattern(metadata.image()));
                    titleLabel.setText(metadata.title());
                    artistLabel.setText(metadata.artist());
                });
            }
        }
    }
}