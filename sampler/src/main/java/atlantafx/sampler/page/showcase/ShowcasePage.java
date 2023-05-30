/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.Page;
import java.net.URI;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public abstract class ShowcasePage extends StackPane implements Page {

    protected static final PseudoClass SHOWCASE_MODE = PseudoClass.getPseudoClass("showcase-mode");
    protected static final int DEFAULT_WIDTH = 800;
    protected static final int DEFAULT_HEIGHT = 600;

    protected final VBox showcaseWindow = new VBox();
    protected final Label windowTitle = new Label();
    protected final VBox showCaseContent = new VBox();
    protected final FontIcon aboutBtn = new FontIcon(Feather.HELP_CIRCLE);
    protected final BooleanProperty maximized = new SimpleBooleanProperty();
    protected int windowWidth = DEFAULT_WIDTH;
    protected int windowHeight = DEFAULT_HEIGHT;

    public ShowcasePage() {
        super();

        createShowcaseLayout();
    }

    protected void createShowcaseLayout() {
        windowTitle.getStyleClass().addAll("title");

        aboutBtn.getStyleClass().addAll(Styles.SMALL, Styles.FLAT);

        var maximizeBtn = new FontIcon(Feather.MAXIMIZE_2);
        maximizeBtn.getStyleClass().addAll(Styles.SMALL, Styles.FLAT);
        maximizeBtn.setOnMouseClicked(e -> maximized.set(!maximized.get()));

        var windowHeader = new HBox(
            20, windowTitle, new Spacer(), aboutBtn, maximizeBtn
        );
        windowHeader.getStyleClass().add("header");

        showcaseWindow.getStyleClass().add("window");
        showcaseWindow.getChildren().setAll(windowHeader, showCaseContent);
        VBox.setVgrow(showCaseContent, Priority.ALWAYS);

        getStyleClass().add("showcase-page");
        getChildren().setAll(showcaseWindow);

        maximized.addListener((obs, old, val) -> {
            getScene().getRoot().pseudoClassStateChanged(SHOWCASE_MODE, val);
            maximizeBtn.setIconCode(val ? Feather.MINIMIZE_2 : Feather.MAXIMIZE_2);

            var width = val ? -1 : windowWidth;
            var height = val ? -1 : windowHeight;
            showcaseWindow.setMinSize(width, height);
            showcaseWindow.setMaxSize(width, height);
        });
    }

    protected void setWindowTitle(String text, @Nullable Node graphic) {
        windowTitle.setText(Objects.requireNonNull(text, "text"));
        if (graphic != null) {
            windowTitle.setGraphic(graphic);
        }
    }

    protected void setAboutInfo(String text) {
        var tooltip = new Tooltip(text);
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(300);
        aboutBtn.setOnMouseEntered(e -> {
            var localBounds = aboutBtn.getBoundsInLocal();
            var screenBounds = aboutBtn.localToScreen(localBounds);
            tooltip.show(getScene().getWindow(), screenBounds.getCenterX(), screenBounds.getMaxY());
        });
        aboutBtn.setOnMouseExited(e -> tooltip.hide());
    }

    protected void setShowCaseContent(Node node) {
        setShowCaseContent(node, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    protected void setShowCaseContent(Node node, int width, int height) {
        Objects.requireNonNull(node, "node");

        this.windowWidth = width > 0 ? width : DEFAULT_WIDTH;
        this.windowHeight = height > 0 ? height : DEFAULT_HEIGHT;
        showcaseWindow.setMinSize(width, height);
        showcaseWindow.setMaxSize(width, height);

        showCaseContent.getChildren().setAll(node);
        VBox.setVgrow(node, Priority.ALWAYS);
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return false;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    @Override
    public Node getSnapshotTarget() {
        return showCaseContent;
    }

    @Override
    public void reset() {
    }

    protected void showWarning(String header, String description) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(description);
        alert.initOwner(getScene().getWindow());
        alert.initStyle(StageStyle.DECORATED);
        alert.showAndWait();
    }
}
