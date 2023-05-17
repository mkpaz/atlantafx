/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static atlantafx.sampler.util.Containers.setScrollConstraints;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.layout.Overlay;
import java.net.URI;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPage extends StackPane implements Page {

    protected final VBox userContent = new VBox();
    protected Overlay overlay;
    protected boolean isRendered = false;

    protected AbstractPage() {
        super();

        userContent.getStyleClass().add("user-content");
        getStyleClass().add("page");

        createPageLayout();
    }

    protected void createPageLayout() {
        var userContentArea = new StackPane(userContent);
        userContentArea.setAlignment(Pos.TOP_CENTER);
        userContent.setMinWidth(Math.min(Page.MAX_WIDTH, 800));
        userContent.setMaxWidth(Math.min(Page.MAX_WIDTH, 800));

        var scrollPane = new ScrollPane(userContentArea);
        setScrollConstraints(scrollPane, AS_NEEDED, true, NEVER, true);
        scrollPane.setMaxHeight(20_000);

        getChildren().setAll(scrollPane);
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return true;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return true;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return URI.create(String.format(JFX_JAVADOC_URI_TEMPLATE, "control/" + getName()));
    }

    @Override
    public void reset() {
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (isRendered) {
            return;
        }

        isRendered = true;
        onRendered();
    }

    // Some properties can only be obtained after node placed
    // to the scene graph and here is the place do this.
    protected void onRendered() {
        this.overlay = lookupOverlay();
    }

    protected void addPageHeader() {
        var pageHeader = new PageHeader(this);
        userContent.getChildren().add(pageHeader);
    }

    protected void addNode(Node node) {
        userContent.getChildren().add(node);
    }

    protected void addFormattedText(String text) {
        userContent.getChildren().add(BBCodeParser.createFormattedText(text));
    }

    protected Overlay lookupOverlay() {
        return getScene() != null
            && getScene().lookup("." + Overlay.STYLE_CLASS) instanceof Overlay ov ? ov : null;
    }
}
