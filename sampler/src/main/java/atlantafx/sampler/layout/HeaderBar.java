/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static atlantafx.base.theme.Styles.TITLE_3;
import static atlantafx.base.theme.Styles.TITLE_4;
import static atlantafx.sampler.Launcher.IS_DEV_MODE;
import static atlantafx.sampler.layout.MainLayer.SIDEBAR_WIDTH;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.Resources;
import atlantafx.sampler.event.BrowseEvent;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.HotkeyEvent;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

class HeaderBar extends HBox {

    private static final int HEADER_HEIGHT = 50;
    private static final Ikon ICON_CODE = Feather.CODE;
    private static final Ikon ICON_SAMPLE = Feather.LAYOUT;

    private final MainModel model;
    private Consumer<Node> quickConfigActionHandler;
    private Overlay overlay;
    private SearchDialog searchDialog;

    public HeaderBar(MainModel model) {
        super();

        this.model = model;

        createView();
    }

    private void createView() {
        var logoImage = new ImageView(new Image(Resources.getResource("assets/app-icon.png").toString()));
        logoImage.setFitWidth(32);
        logoImage.setFitHeight(32);

        var logoImageBorder = new Insets(1);
        var logoImageBox = new StackPane(logoImage);
        logoImageBox.getStyleClass().add("image");
        logoImageBox.setPadding(logoImageBorder);
        logoImageBox.setPrefWidth(logoImage.getFitWidth() + logoImageBorder.getRight() * 2);
        logoImageBox.setMaxWidth(logoImage.getFitHeight() + logoImageBorder.getTop() * 2);
        logoImageBox.setPrefHeight(logoImage.getFitWidth() + logoImageBorder.getTop() * 2);
        logoImageBox.setMaxHeight(logoImage.getFitHeight() + logoImageBorder.getRight() * 2);

        var logoLabel = new Label("AtlantaFX");
        logoLabel.getStyleClass().addAll(TITLE_3);

        var versionLabel = new Label(System.getProperty("app.version"));
        versionLabel.getStyleClass().addAll("version", TEXT_SMALL);

        var logoBox = new HBox(10, logoImageBox, logoLabel, versionLabel);
        logoBox.getStyleClass().add("logo");
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setMinWidth(SIDEBAR_WIDTH);
        logoBox.setPrefWidth(SIDEBAR_WIDTH);
        logoBox.setMaxWidth(SIDEBAR_WIDTH);

        var titleLabel = new Label();
        titleLabel.getStyleClass().addAll("page-title", TITLE_4);
        titleLabel.textProperty().bind(model.titleProperty());

        var searchBox = new HBox(10,
            new FontIcon(Material2MZ.SEARCH),
            new Text("Search"),
            new Spacer(5),
            new Label("/")
        );
        searchBox.getStyleClass().add("box");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        var searchButton = new Button();
        searchButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        searchButton.getStyleClass().add("search-button");
        searchButton.setGraphic(searchBox);
        searchButton.setOnAction(e -> openSearchDialog());

        DefaultEventBus.getInstance().subscribe(HotkeyEvent.class, e -> {
            if (e.getKeys().getCode() == KeyCode.SLASH) {
                openSearchDialog();
            }
        });

        // this dummy anchor prevents popover from inheriting
        // unwanted styles from the owner node
        var popoverAnchor = new Region();

        var quickConfigBtn = new FontIcon(Material2OutlinedMZ.STYLE);
        quickConfigBtn.mouseTransparentProperty().bind(model.themeChangeToggleProperty().not());
        quickConfigBtn.opacityProperty().bind(Bindings.createDoubleBinding(
            () -> model.themeChangeToggleProperty().get() ? 1.0 : 0.5, model.themeChangeToggleProperty()
        ));
        quickConfigBtn.setOnMouseClicked(e -> {
            if (quickConfigActionHandler != null) {
                quickConfigActionHandler.accept(popoverAnchor);
            }
        });

        var sourceCodeBtn = new FontIcon(ICON_CODE);
        sourceCodeBtn.mouseTransparentProperty().bind(model.sourceCodeToggleProperty().not());
        sourceCodeBtn.opacityProperty().bind(Bindings.createDoubleBinding(
            () -> model.sourceCodeToggleProperty().get() ? 1.0 : 0.5, model.sourceCodeToggleProperty()
        ));
        sourceCodeBtn.setOnMouseClicked(e -> model.nextSubLayer());

        var githubLink = new FontIcon(Feather.GITHUB);
        githubLink.getStyleClass().addAll("github");
        githubLink.setOnMouseClicked(e -> {
            var homepage = System.getProperty("app.homepage");
            if (homepage != null) {
                DefaultEventBus.getInstance().publish(new BrowseEvent(URI.create(homepage)));
            }
        });

        // ~

        model.currentSubLayerProperty().addListener((obs, old, val) -> {
            switch (val) {
                case PAGE -> sourceCodeBtn.setIconCode(ICON_CODE);
                case SOURCE_CODE -> sourceCodeBtn.setIconCode(ICON_SAMPLE);
            }
        });

        setId("header-bar");
        setMinHeight(HEADER_HEIGHT);
        setPrefHeight(HEADER_HEIGHT);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().setAll(
            logoBox,
            titleLabel,
            new Spacer(),
            searchButton,
            popoverAnchor,
            quickConfigBtn,
            sourceCodeBtn,
            githubLink
        );

        if (IS_DEV_MODE) {
            var devModeLabel = new Label("app is running in development mode");
            devModeLabel.getStyleClass().addAll(TEXT_SMALL, "dev-mode-indicator");
            getChildren().add(2, devModeLabel);
        }
    }

    void setQuickConfigActionHandler(Consumer<Node> handler) {
        this.quickConfigActionHandler = handler;
    }

    private Overlay lookupOverlay() {
        return Objects.requireNonNullElse(overlay,
            overlay = getScene() != null && getScene().lookup("." + Overlay.STYLE_CLASS) instanceof Overlay o ? o : null
        );
    }

    private void openSearchDialog() {
        if (searchDialog == null) {
            searchDialog = new SearchDialog(model);
            searchDialog.setOnCloseRequest(() -> {
                var overlay = lookupOverlay();
                overlay.removeContent();
                overlay.toBack();
            });
        }

        var overlay = lookupOverlay();
        overlay.setContent(searchDialog, HPos.CENTER);
        overlay.toFront();
        Platform.runLater(() -> searchDialog.begForFocus());
    }
}
