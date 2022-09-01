/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import atlantafx.base.controls.Popover;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.layout.Overlay;
import atlantafx.sampler.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import net.datafaker.Faker;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static atlantafx.base.controls.Popover.ArrowLocation.TOP_CENTER;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.sampler.util.Containers.setScrollConstraints;

public abstract class AbstractPage extends BorderPane implements Page {

    protected static final int HEADER_HEIGHT = 50;

    protected static final Faker FAKER = new Faker();
    protected static final Random RANDOM = new Random();
    protected static final EventHandler<ActionEvent> PRINT_SOURCE = System.out::println;
    private static final Ikon ICON_CODE = Feather.CODE;
    private static final Ikon ICON_SAMPLE = Feather.LAYOUT;

    protected Button quickConfigBtn;
    protected Popover quickConfigPopover;
    protected Button sourceCodeToggleBtn;
    protected StackPane codeViewerWrapper;
    protected CodeViewer codeViewer;
    protected VBox userContent;
    protected Overlay overlay;
    protected boolean isRendered = false;

    protected AbstractPage() {
        super();

        getStyleClass().add("page");
        createPageLayout();
    }

    protected void createPageLayout() {
        // == header ==

        var titleLabel = new Label(getName());
        titleLabel.getStyleClass().addAll(Styles.TITLE_4);

        codeViewer = new CodeViewer();

        codeViewerWrapper = new StackPane();
        codeViewerWrapper.getStyleClass().add("wrapper");
        codeViewerWrapper.getChildren().setAll(codeViewer);

        quickConfigBtn = new Button("", new FontIcon(Material2OutlinedMZ.STYLE));
        quickConfigBtn.getStyleClass().addAll(BUTTON_ICON, FLAT);
        quickConfigBtn.setTooltip(new Tooltip("Change theme"));
        quickConfigBtn.setOnAction(e -> showThemeConfigPopover());

        sourceCodeToggleBtn = new Button("", new FontIcon(ICON_CODE));
        sourceCodeToggleBtn.getStyleClass().addAll(BUTTON_ICON, FLAT);
        sourceCodeToggleBtn.setTooltip(new Tooltip("Source code"));
        sourceCodeToggleBtn.setOnAction(e -> toggleSourceCode());

        var header = new HBox(30);
        header.getStyleClass().add("header");
        header.setMinHeight(HEADER_HEIGHT);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().setAll(titleLabel, new Spacer(), quickConfigBtn, sourceCodeToggleBtn);

        // == user content ==

        userContent = new VBox();
        userContent.getStyleClass().add("user-content");

        var userContentWrapper = new StackPane();
        userContentWrapper.getStyleClass().add("wrapper");
        userContentWrapper.getChildren().setAll(userContent);

        var scrollPane = new ScrollPane(userContentWrapper);
        setScrollConstraints(scrollPane,
                ScrollPane.ScrollBarPolicy.AS_NEEDED, true,
                ScrollPane.ScrollBarPolicy.AS_NEEDED, true
        );
        scrollPane.setMaxHeight(10_000);

        // == layout ==

        var stackPane = new StackPane();
        stackPane.getStyleClass().add("stack");
        stackPane.getChildren().addAll(codeViewerWrapper, scrollPane);

        setTop(header);
        setCenter(stackPane);
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public void reset() { }

    protected void layoutChildren() {
        super.layoutChildren();
        if (isRendered) { return; }

        isRendered = true;
        onRendered();
    }

    // Some properties can only be obtained after node placed
    // to the scene graph and here is the place do this.
    protected void onRendered() {
        this.overlay = lookupOverlay();
    }

    protected Overlay lookupOverlay() {
        return getScene() != null && getScene().lookup("." + Overlay.STYLE_CLASS) instanceof Overlay overlay ?
                overlay : null;
    }

    protected void showThemeConfigPopover() {
        if (quickConfigPopover == null) {
            var content = new QuickConfigMenu();
            content.setExitHandler(() -> quickConfigPopover.hide());

            quickConfigPopover = new Popover(content);
            quickConfigPopover.setHeaderAlwaysVisible(false);
            quickConfigPopover.setDetachable(false);
            quickConfigPopover.setArrowLocation(TOP_CENTER);
        }

        quickConfigPopover.show(quickConfigBtn);
    }

    protected void toggleSourceCode() {
        var graphic = (FontIcon) sourceCodeToggleBtn.getGraphic();

        if (graphic.getIconCode() == ICON_SAMPLE) {
            codeViewerWrapper.toBack();
            graphic.setIconCode(ICON_CODE);
            return;
        }

        var sourceFileName = getClass().getSimpleName() + ".java";
        try (var stream = getClass().getResourceAsStream(sourceFileName)) {
            Objects.requireNonNull(stream, "Missing source file '" + sourceFileName + "';");

            // set syntax highlight theme according to JavaFX theme
            ThemeManager tm = ThemeManager.getInstance();
            codeViewer.setContent(stream, tm.getMatchingHighlightJSTheme(tm.getTheme()));

            graphic.setIconCode(ICON_SAMPLE);
            codeViewerWrapper.toFront();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //  Helpers                                                              //
    ///////////////////////////////////////////////////////////////////////////

    protected <T> List<T> generate(Supplier<T> supplier, int count) {
        return Stream.generate(supplier).limit(count).collect(Collectors.toList());
    }

    protected Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }
}
