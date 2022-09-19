/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.sampler.event.BrowseEvent;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.HotkeyEvent;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URI;
import java.util.function.Consumer;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.sampler.Launcher.IS_DEV_MODE;
import static atlantafx.sampler.layout.MainLayer.SIDEBAR_WIDTH;

class HeaderBar extends HBox {

    private static final int HEADER_HEIGHT = 50;
    private static final Ikon ICON_CODE = Feather.CODE;
    private static final Ikon ICON_SAMPLE = Feather.LAYOUT;

    private final MainModel model;
    private Consumer<Node> quickConfigActionHandler;

    public HeaderBar(MainModel model) {
        super();
        this.model = model;
        createView();
    }

    private void createView() {
        var logoLabel = new Label("AtlantaFX");
        logoLabel.getStyleClass().addAll(TITLE_3);

        var versionLabel = new Label(System.getProperty("app.version"));
        versionLabel.getStyleClass().addAll("version", TEXT_SMALL);

        var githubLink = new FontIcon(Feather.GITHUB);
        githubLink.getStyleClass().addAll("github");
        githubLink.setOnMouseClicked(e -> {
            var homepage = System.getProperty("app.homepage");
            if (homepage != null) {
                DefaultEventBus.getInstance().publish(new BrowseEvent(URI.create(homepage)));
            }
        });

        var logoBox = new HBox(10, logoLabel, versionLabel, new Spacer(), githubLink);
        logoBox.getStyleClass().add("logo");
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setMinWidth(SIDEBAR_WIDTH);
        logoBox.setPrefWidth(SIDEBAR_WIDTH);
        logoBox.setMaxWidth(SIDEBAR_WIDTH);

        var titleLabel = new Label();
        titleLabel.getStyleClass().addAll("page-title", TITLE_4);
        titleLabel.textProperty().bind(model.titleProperty());

        var searchField = new CustomTextField();
        searchField.setLeft(new FontIcon(Material2MZ.SEARCH));
        searchField.setPromptText("Search");
        model.searchTextProperty().bind(searchField.textProperty());

        DefaultEventBus.getInstance().subscribe(HotkeyEvent.class, e -> {
            if (e.getKeys().getControl() == ModifierValue.DOWN && e.getKeys().getCode() == KeyCode.F) {
                searchField.requestFocus();
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
            if (quickConfigActionHandler != null) { quickConfigActionHandler.accept(popoverAnchor); }
        });

        var sourceCodeBtn = new FontIcon(ICON_CODE);
        sourceCodeBtn.mouseTransparentProperty().bind(model.sourceCodeToggleProperty().not());
        sourceCodeBtn.opacityProperty().bind(Bindings.createDoubleBinding(
                () -> model.sourceCodeToggleProperty().get() ? 1.0 : 0.5, model.sourceCodeToggleProperty()
        ));
        sourceCodeBtn.setOnMouseClicked(e -> model.nextSubLayer());

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
                searchField,
                popoverAnchor,
                quickConfigBtn,
                sourceCodeBtn
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
}
