/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Theme;
import atlantafx.decorations.Alignment;
import atlantafx.decorations.Decoration;
import atlantafx.decorations.HeaderButtonGroup;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.components.MenuBarPage;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.theme.Themes;
import java.net.URI;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

public final class DecorationsPage extends AbstractPage {

    public static final String NAME = "Decorations";

    private static final int SAMPLE_WIDTH = 300;
    private static final int SAMPLE_HEIGHT = 50;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    public DecorationsPage() {
        super();

        addPageHeader();
        addFormattedText("""
            AtlantaFX provides a separate module to support custom window controls
            for the JavaFX [i]StageStyle.EXTENDED[/i]. You can find a usage example in the
            [i]HeaderButtonGroup[/i] javadoc."""
        );
        addNode(stageRunner());
        addNode(overview());
    }

    private Node stageRunner() {
        var btn = new Button("Test in Stage");
        btn.getStyleClass().addAll(Styles.ACCENT);
        btn.setOnAction(e -> showDecoratedStage());

        var root = new HBox(btn);
        root.setAlignment(Pos.CENTER_LEFT);

        return root;
    }

    private Node overview() {
        var decorations = new FlowPane(
            HGAP_10, VGAP_10,
            createSubScene(Themes.PRIMER_LIGHT.getInstance(), Decoration.CHROME_OS_LIGHT, Alignment.TRAILING),
            createSubScene(Themes.PRIMER_DARK.getInstance(), Decoration.CHROME_OS_DARK, Alignment.TRAILING),
            createSubScene(Themes.NORD_LIGHT.getInstance(), Decoration.DEXY_LIGHT, Alignment.TRAILING),
            createSubScene(Themes.NORD_DARK.getInstance(), Decoration.DEXY_DARK, Alignment.TRAILING),
            createSubScene(Themes.PRIMER_LIGHT.getInstance(), Decoration.FLUENT_LIGHT, Alignment.TRAILING),
            createSubScene(Themes.PRIMER_DARK.getInstance(), Decoration.FLUENT_DARK, Alignment.TRAILING),
            createSubScene(Themes.DRACULA.getInstance(), Decoration.DRACULA, Alignment.TRAILING),
            createSubScene(Themes.NORD_DARK.getInstance(), Decoration.NORD_DARK, Alignment.TRAILING),
            createSubScene(Themes.PRIMER_LIGHT.getInstance(), Decoration.GENOME_LIGHT, Alignment.TRAILING),
            createSubScene(Themes.PRIMER_DARK.getInstance(), Decoration.GENOME_DARK, Alignment.TRAILING),
            createSubScene(Themes.CUPERTINO_LIGHT.getInstance(), Decoration.MAC_SEQUOIA_LIGHT, Alignment.LEADING),
            createSubScene(Themes.CUPERTINO_DARK.getInstance(), Decoration.MAC_SEQUOIA_DARK, Alignment.LEADING),
            createSubScene(Themes.PRIMER_LIGHT.getInstance(), Decoration.WIN10_LIGHT, Alignment.TRAILING),
            createSubScene(Themes.PRIMER_DARK.getInstance(), Decoration.WIN10_DARK, Alignment.TRAILING)
        );

        var label = new Label("Overview");
        label.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_BOLD);

        return new VBox(VGAP_10, label, decorations);
    }

    @SuppressWarnings("deprecation")
    private void showDecoratedStage() {
        var stage = new Stage();

        // header
        var logo = new ImageView(
            new Image(Resources.getResource("images/devicons/code.png").toString())
        );

        var menuBar = new MenuBarPage.ExampleMenuBar();
        menuBar.setStyle("-fx-background-color: transparent;");

        var headerBox = new HBox(logo, menuBar);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 0, 10));

        var headerBar = new HeaderBar();
        headerBar.setLeading(headerBox);

        var windowsButtons = HeaderButtonGroup.standardGroup();
        windowsButtons.install(headerBar, stage);

        // content
        var root = new BorderPane();
        root.setTop(headerBar);

        var scene = new Scene(root, 640, 480);
        scene.setUserAgentStylesheet(ThemeManager.DUMMY_STYLESHEET);

        var switcher = new LocalThemeSwitcher(scene);

        var themeChoice = new ComboBox<Theme>();
        themeChoice.getItems().setAll(FXCollections.observableArrayList(Themes.getAll()));
        themeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Theme theme) {
                return theme != null ? theme.getName() : "";
            }

            @Override
            public Theme fromString(String string) {
                return null;
            }
        });
        themeChoice.setPrefWidth(200);

        var decorationChoice = new ComboBox<Decoration>();
        decorationChoice.getItems().setAll(FXCollections.observableArrayList(Decoration.values()));
        decorationChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Decoration decoration) {
                return decoration != null ? decoration.getName() : "";
            }

            @Override
            public Decoration fromString(String string) {
                return null;
            }
        });
        decorationChoice.setPrefWidth(200);

        var switchesBox = new VBox(VGAP_20, themeChoice, decorationChoice);
        switchesBox.setAlignment(Pos.CENTER);
        root.setCenter(switchesBox);

        // initial theme
        var managerTheme = ThemeManager.getInstance().getTheme();
        var theme = Themes.getByName(managerTheme.getName())
                        .orElse(managerTheme.isDarkMode() // if custom theme
                                    ? Themes.PRIMER_DARK.getInstance()
                                    : Themes.PRIMER_LIGHT.getInstance()
                        );
        switcher.setTheme(theme);
        themeChoice.getSelectionModel().select(theme);

        var decoration = theme.isDarkMode() ? Decoration.WIN10_DARK : Decoration.WIN10_LIGHT;
        switcher.setDecoration(decoration);
        decorationChoice.getSelectionModel().select(decoration);

        // listeners
        themeChoice.getSelectionModel().selectedItemProperty().subscribe(switcher::setTheme);
        decorationChoice.getSelectionModel().selectedItemProperty().subscribe(switcher::setDecoration);

        // stage
        stage.initStyle(StageStyle.EXTENDED);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> windowsButtons.uninstall(headerBar, stage));
        stage.show();
    }

    private SubScene createSubScene(Theme theme, Decoration decoration, Alignment align) {
        var label = new Label(decoration.getName());
        label.setPadding(new Insets(5, 10, 5, 10));
        label.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_BOLD);

        var root = new HBox();
        if (align == Alignment.TRAILING) {
            root.getChildren().setAll(
                label,
                new Spacer(),
                HeaderButtonGroup.standardGroup()
            );
        } else {
            root.getChildren().setAll(
                HeaderButtonGroup.standardGroup(),
                label
            );
            root.setSpacing(20);
            root.setAlignment(Pos.TOP_LEFT);
        }
        root.setAlignment(Pos.TOP_LEFT);
        root.setMinSize(SAMPLE_WIDTH, SAMPLE_HEIGHT);
        root.setStyle("""
            -fx-background-color: -color-bg-default;
            -fx-border-width: 1px;
            -fx-border-color: -color-border-default;"""
        );

        var subScene = new SubScene(root, SAMPLE_WIDTH, SAMPLE_HEIGHT);
        subScene.setUserAgentStylesheet(ThemeManager.DUMMY_STYLESHEET);
        root.getStylesheets().addAll(
            theme.getUserAgentStylesheet(),
            decoration.getStylesheet()
        );

        return subScene;
    }

    //=========================================================================

    private static class LocalThemeSwitcher {

        private final Scene scene;
        private Theme theme;
        private Decoration decoration;

        public LocalThemeSwitcher(Scene scene) {
            this.scene = scene;
        }

        public void setTheme(@Nullable Theme t) {
            if (theme != null) {
                scene.getStylesheets().remove(theme.getUserAgentStylesheet());
            }

            this.theme = t;
            scene.getStylesheets().add(theme.getUserAgentStylesheet());
        }

        public void setDecoration(Decoration d) {
            if (decoration != null) {
                scene.getStylesheets().remove(decoration.getStylesheet());
            }

            this.decoration = d;
            scene.getStylesheets().add(decoration.getStylesheet());
        }
    }
}
