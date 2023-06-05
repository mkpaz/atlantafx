/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.util.NodeUtils;
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
    protected final StackPane userContentArea = new StackPane(userContent);
    protected boolean isRendered = false;

    protected AbstractPage() {
        super();

        userContent.getStyleClass().add("user-content");
        getStyleClass().add("page");

        createPageLayout();
    }

    protected void createPageLayout() {
        userContentArea.setAlignment(Pos.TOP_CENTER);
        userContent.setMinWidth(Math.min(Page.MAX_WIDTH, 800));
        userContent.setMaxWidth(Math.min(Page.MAX_WIDTH, 800));

        var scrollPane = new ScrollPane(userContentArea);
        NodeUtils.setScrollConstraints(scrollPane, AS_NEEDED, true, NEVER, true);
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
    public @Nullable URI getJavadocUri() {
        return URI.create(String.format(JFX_JAVADOC_URI_TEMPLATE, "control/" + getName()));
    }

    @Override
    public Node getSnapshotTarget() {
        return userContentArea;
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
}
