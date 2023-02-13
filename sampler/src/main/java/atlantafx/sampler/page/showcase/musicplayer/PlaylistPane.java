/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.musicplayer;

import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.SMALL;
import static atlantafx.base.theme.Styles.TEXT_CAPTION;
import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static atlantafx.sampler.page.showcase.musicplayer.MediaFile.Metadata.NO_IMAGE_ALT;
import static atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage.SUPPORTED_MEDIA_TYPES;
import static java.lang.Double.MAX_VALUE;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.stage.FileChooser.ExtensionFilter;
import static org.kordamp.ikonli.material2.Material2AL.ADD;
import static org.kordamp.ikonli.material2.Material2MZ.PLAYLIST_PLAY;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Tweaks;
import java.io.File;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

final class PlaylistPane extends VBox {

    private final Model model;

    private Label sizeLabel;
    private Label sizeDescLabel;
    private ProgressBar loadProgress;
    private Button addButton;
    private ListView<MediaFile> playlist;

    public PlaylistPane(Model model) {
        super();

        this.model = model;

        createView();
        init();
    }

    private void createView() {
        var headerLabel = new Label("Playlist");
        headerLabel.getStyleClass().setAll(TEXT_CAPTION);
        // There's probably some #javafx-bug here. This label uses CSS class that
        // changes font size and weight. When switching between themes it _sometimes_
        // ignores new color variables and continues using old fg color. Like it
        // caches old font or something. The below rule forces it to use proper color.
        headerLabel.setStyle("-fx-text-fill: -color-fg-default;");

        sizeLabel = new Label("");
        sizeLabel.getStyleClass().add(TEXT_SMALL);

        sizeDescLabel = new Label("empty");
        sizeDescLabel.getStyleClass().add(TEXT_SMALL);

        loadProgress = new ProgressBar(1);
        loadProgress.getStyleClass().add(SMALL);
        loadProgress.setMaxWidth(MAX_VALUE);
        loadProgress.setVisible(false);

        addButton = new Button("Add", new FontIcon(ADD));
        addButton.getStyleClass().add(FLAT);

        var controlsBox = new HBox();
        controlsBox.setPadding(new Insets(0, 0, 10, 0));
        controlsBox.getStyleClass().add("controls");
        controlsBox.getChildren().setAll(
            new VBox(5, headerLabel, sizeDescLabel),
            new Spacer(),
            addButton
        );
        controlsBox.setAlignment(CENTER_LEFT);

        playlist = new ListView<>(model.playlist());
        playlist.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        playlist.setCellFactory(param -> new MediaCell(model));
        playlist.setPlaceholder(new Label("No Content"));
        VBox.setVgrow(playlist, ALWAYS);

        getStyleClass().add("playlist");
        setPadding(new Insets(10));
        setSpacing(5);
        getChildren().setAll(controlsBox, loadProgress, playlist);
    }

    private void init() {
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

        addButton.setOnAction(e -> {
            var extensions = SUPPORTED_MEDIA_TYPES.stream().map(s -> "*." + s).toList();
            var fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter(
                "MP3 files (" + String.join(", ", extensions) + ")",
                extensions
            ));
            List<File> files = fileChooser.showOpenMultipleDialog(getScene().getWindow());
            if (files == null || files.isEmpty()) {
                return;
            }

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

        private static final PseudoClass PLAYING = PseudoClass.getPseudoClass("playing");

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
                if (getItem() != null) {
                    model.play(getItem());
                }
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

                var playing = Objects.equals(mediaFile, model.currentTrackProperty().get());

                pseudoClassStateChanged(PLAYING, playing);
                playMark.setVisible(playing);

                mediaFile.readMetadata(metadata -> {
                    coverImage.setFill(new ImagePattern(
                        metadata.image() != null ? metadata.image() : NO_IMAGE_ALT
                    ));
                    titleLabel.setText(metadata.title());
                    artistLabel.setText(metadata.artist());
                });
            }
        }
    }
}
