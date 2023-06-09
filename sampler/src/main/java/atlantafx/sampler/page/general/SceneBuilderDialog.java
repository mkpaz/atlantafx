/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.base.theme.Styles.TEXT_CAPTION;
import static atlantafx.base.theme.Styles.TEXT_MUTED;

import atlantafx.base.controls.Spacer;
import atlantafx.base.layout.DeckPane;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.Resources;
import atlantafx.sampler.event.BrowseEvent;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.layout.ModalDialog;
import atlantafx.sampler.page.general.SceneBuilderDialogModel.Screen;
import atlantafx.sampler.util.NodeUtils;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

class SceneBuilderDialog extends ModalDialog {

    private final DeckPane deck;
    private final Button backBtn;
    private final Button forwardBtn;
    private final Button closeBtn;

    private Pane startScreen;
    private Pane actionScreen;
    private Pane themeScreen;
    private Pane execScreen;
    private Pane reportScreen;

    private final SceneBuilderDialogModel model = new SceneBuilderDialogModel();

    public SceneBuilderDialog() {
        super();

        deck = createContent();

        backBtn = new Button("Previous", new FontIcon(Material2AL.ARROW_BACK));

        forwardBtn = new Button("Next", new FontIcon(Material2AL.ARROW_FORWARD));
        forwardBtn.setContentDisplay(ContentDisplay.RIGHT);

        closeBtn = new Button("Close");
        NodeUtils.toggleVisibility(closeBtn, false);

        var footer = new HBox(10);
        footer.getChildren().setAll(backBtn, new Spacer(), forwardBtn, closeBtn);
        footer.getStyleClass().add("footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        VBox.setVgrow(footer, Priority.NEVER);

        header.setTitle("SceneBuilder Integration");
        content.setBody(deck);
        content.setFooter(footer);
        content.setPrefSize(600, 440);

        init();
    }

    private DeckPane createContent() {
        startScreen = createStartScreen();
        actionScreen = createActionScreen();
        themeScreen = createThemeScreen();
        execScreen = createExecScreen();
        reportScreen = createReportScreen();

        var deck = new DeckPane();
        deck.addChildren(Insets.EMPTY, startScreen, actionScreen, themeScreen, execScreen, reportScreen);
        deck.setAnimationDuration(Duration.millis(250));
        deck.setId("scene-builder-wizard");

        return deck;
    }

    private void init() {
        deck.setTopNode(startScreen);

        model.activeScreenProperty().addListener((obs, old, val) -> {
            if (val == null) {
                deck.resetTopNode();
                return;
            }

            var nextScreen = getScreenView(val);
            if (old == null || old.ordinal() < val.ordinal()) {
                deck.swipeLeft(nextScreen);
            } else {
                deck.swipeRight(nextScreen);
            }

            if (val == Screen.REPORT) {
                NodeUtils.toggleVisibility(closeBtn, true);
                NodeUtils.toggleVisibility(forwardBtn, false);
            } else {
                NodeUtils.toggleVisibility(closeBtn, false);
                NodeUtils.toggleVisibility(forwardBtn, true);
            }
        });

        backBtn.setOnAction(e -> model.back());
        backBtn.visibleProperty().bind(model.canGoBackProperty());

        forwardBtn.setOnAction(e -> model.forward());
        forwardBtn.disableProperty().bind(model.canGoForwardProperty().not());

        closeBtn.setOnAction(e -> close());
    }

    void reset() {
        model.reset();
    }

    private Pane getScreenView(Screen screen) {
        return switch (screen) {
            case START -> startScreen;
            case ACTION -> actionScreen;
            case THEME -> themeScreen;
            case EXEC -> execScreen;
            case REPORT -> reportScreen;
        };
    }

    private Pane createStartScreen() {
        var previewImg = new ImageView(new Image(
            Resources.getResourceAsStream("images/scene-builder/scene-builder-in-action.jpg")
        ));
        previewImg.setFitWidth(280);
        previewImg.setFitHeight(190);

        var previewLbl = new Label("""
            SceneBuilder is a visual layout tool that lets users quickly \
            design JavaFX application user interfaces, without coding.
            """);
        previewLbl.setWrapText(true);

        var downloadLnk = new Hyperlink("Get SceneBuilder");
        downloadLnk.setGraphic(new FontIcon(Material2OutlinedAL.LINK));
        downloadLnk.setOnAction(e -> DefaultEventBus.getInstance().publish(
            new BrowseEvent(URI.create("https://gluonhq.com/products/scene-builder/"))
        ));

        var previewBox = new HBox(20, previewImg, new VBox(20, previewLbl, downloadLnk));
        previewBox.setAlignment(Pos.TOP_LEFT);

        var browseLbl = new Label("Select the SceneBuilder installation directory:");
        browseLbl.getStyleClass().addAll(TEXT_CAPTION, TEXT_MUTED);

        var browseBtn = new Button("Browse", new FontIcon(Material2OutlinedAL.FOLDER));
        browseBtn.setMinWidth(120);
        browseBtn.setOnAction(e -> {
            var dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(SceneBuilderInstaller.getDefaultConfigDir().toFile());
            File dir = dirChooser.showDialog(getScene().getWindow());
            if (dir != null) {
                model.setInstallDir(dir.toPath());
            }
        });

        var installDirLbl = new Label();
        installDirLbl.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        installDirLbl.textProperty().bind(model.installDirProperty().map(
            path -> path.toAbsolutePath().toString()
        ));
        HBox.setHgrow(installDirLbl, Priority.ALWAYS);

        var installDirBox = new HBox(10, browseBtn, installDirLbl);
        installDirBox.setAlignment(Pos.CENTER_LEFT);

        var noticeLbl = new Label("""
            You must have write permission to the directory. \
            Installation files will be overwritten, but you can rollback changes using the same dialog again.
            """);
        noticeLbl.setWrapText(true);
        noticeLbl.setMaxWidth(Region.USE_PREF_SIZE);
        noticeLbl.setMinHeight(Region.USE_PREF_SIZE);

        // ~

        var root = new VBox();
        root.getChildren().setAll(
            previewBox,
            new Spacer(20, Orientation.VERTICAL),
            browseLbl,
            installDirBox,
            noticeLbl
        );
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("screen");

        return root;
    }

    private Pane createActionScreen() {
        var icon = new ImageView(new Image(
            Resources.getResourceAsStream("images/question.png")
        ));
        icon.setFitWidth(64);
        icon.setFitHeight(64);

        var iconBox = new HBox(icon);
        iconBox.setAlignment(Pos.CENTER);

        var msgLbl = new Label("AtlantaFX theme pack is already installed.");

        var updateRadio = new RadioButton("Update or install other themes");
        updateRadio.setToggleGroup(model.getActionGroup());
        updateRadio.setSelected(true);
        updateRadio.setUserData(SceneBuilderDialogModel.ACTION_INSTALL);

        var rollbackRadio = new RadioButton("Uninstall AtlantaFX themes from SceneBuilder");
        rollbackRadio.setToggleGroup(model.getActionGroup());
        rollbackRadio.setUserData(SceneBuilderDialogModel.ACTION_ROLLBACK);

        // ~

        var root = new VBox();
        root.getChildren().setAll(
            iconBox,
            new Spacer(20, Orientation.VERTICAL),
            msgLbl,
            updateRadio,
            rollbackRadio
        );
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("screen");

        return root;
    }

    private Pane createThemeScreen() {
        var icon = new ImageView(new Image(
            Resources.getResourceAsStream("images/scene-builder/color-palette.png")
        ));
        icon.setFitWidth(64);
        icon.setFitHeight(64);

        var iconBox = new HBox(icon);
        iconBox.setAlignment(Pos.CENTER);

        var msgLbl = new Label("Please select up to four themes to install.");

        final var checkBoxes = new ArrayList<CheckBox>();
        final var listener = new ChangeListener<Boolean>() {

            private int activeCount = 0;

            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean old, Boolean val) {
                if (val) {
                    activeCount++;
                    if (activeCount == model.getSceneBuilderThemes().size() - 1) {
                        for (var cb : checkBoxes) {
                            if (!cb.isSelected()) {
                                cb.setDisable(true);
                            }
                        }
                    }
                } else {
                    if (activeCount == model.getSceneBuilderThemes().size() - 1) {
                        for (var cb : checkBoxes) {
                            cb.setDisable(false);
                        }
                    }
                    activeCount--;
                }

                model.notifyThemeToggleStateChanged();
            }
        };

        model.getThemes().forEach(toggle -> {
            var cb = new CheckBox(toggle.getTheme().getName());
            cb.selectedProperty().bindBidirectional(toggle.selectedProperty());
            cb.setUserData(toggle.getTheme());
            cb.setPrefWidth(250); // 2 columns, so half dialog size
            cb.selectedProperty().addListener(listener);
            checkBoxes.add(cb);
        });

        var checkBoxPane = new FlowPane(20, 10);
        checkBoxPane.getChildren().setAll(checkBoxes);

        var root = new VBox();
        root.getChildren().setAll(
            iconBox,
            new Spacer(20, Orientation.VERTICAL),
            msgLbl,
            checkBoxPane
        );
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("screen");

        return root;
    }

    private Pane createExecScreen() {
        var menuImg = new ImageView(new Image(
            Resources.getResourceAsStream("images/scene-builder/scene-builder-themes.png")
        ));
        menuImg.setFitWidth(280);
        menuImg.setFitHeight(210);
        menuImg.setCursor(Cursor.HAND);

        var test = new ImageView(new Image(
            Resources.getResourceAsStream("images/scene-builder/scene-builder-themes.png")
        ));
        test.setPickOnBounds(true);

        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(test);

        Tooltip.install(menuImg, tooltip);

        var msgLbl = new Label("""
            SceneBuilder doesn't support adding external themes. AtlantaFX themes will \
            replace old and unused Caspian stylesheets.
            """);
        msgLbl.setWrapText(true);

        var docsLbl = new Label("You can find more info about the process in the docs.");
        docsLbl.setWrapText(true);

        var docsLink = new Hyperlink("Documentation");
        docsLink.setGraphic(new FontIcon(Material2OutlinedAL.LINK));
        docsLink.setOnAction(e -> DefaultEventBus.getInstance().publish(
            new BrowseEvent(URI.create("https://mkpaz.github.io/atlantafx/fxml/"))
        ));

        var imageBox = new HBox(20, menuImg, new VBox(20, msgLbl, docsLbl, docsLink));
        imageBox.setAlignment(Pos.TOP_LEFT);

        var mappingLbl = new Label("After the installation themes will be mapped as follows.");
        mappingLbl.getStyleClass().addAll(TEXT_CAPTION, TEXT_MUTED);

        final var mappingGrid = new GridPane();
        mappingGrid.setHgap(20);
        mappingGrid.setVgap(10);

        model.themeMapProperty().addListener((obs, old, val) -> {
            mappingGrid.getChildren().clear();
            if (val != null) {
                var idx = new AtomicInteger(0);
                val.forEach((k, v) -> {
                    mappingGrid.add(new Label(k), 0, idx.get());
                    mappingGrid.add(new FontIcon(Material2AL.EAST), 1, idx.get());
                    mappingGrid.add(new Label(v), 2, idx.getAndIncrement());
                });
            }
        });

        var root = new VBox();
        root.getChildren().setAll(
            imageBox,
            new Spacer(20, Orientation.VERTICAL),
            mappingLbl,
            mappingGrid
        );
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("screen");

        return root;
    }

    private Pane createReportScreen() {
        var infoIcon = new ImageView(new Image(
            Resources.getResourceAsStream("images/info.png")
        ));
        infoIcon.setFitWidth(64);
        infoIcon.setFitHeight(64);

        var warningIcon = new ImageView(new Image(
            Resources.getResourceAsStream("images/warning.png")
        ));
        warningIcon.setFitWidth(64);
        warningIcon.setFitHeight(64);

        var iconBox = new HBox();
        iconBox.setAlignment(Pos.CENTER);

        var msgLbl = new Label();
        msgLbl.setAlignment(Pos.TOP_CENTER);
        msgLbl.getStyleClass().add(Styles.TITLE_4);
        msgLbl.setMaxWidth(400);
        msgLbl.setWrapText(true);

        model.reportProperty().addListener((obs, old, val) -> {
            if (val != null) {
                iconBox.getChildren().setAll(val.error() ? warningIcon : infoIcon);
                msgLbl.setText(val.message());
            } else {
                iconBox.getChildren().clear();
                msgLbl.setText(null);
            }
        });

        // ~

        var root = new VBox();
        root.getChildren().setAll(
            iconBox,
            new Spacer(20, Orientation.VERTICAL),
            msgLbl
        );
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("screen");

        return root;
    }
}
