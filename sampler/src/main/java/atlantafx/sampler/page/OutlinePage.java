/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.util.NodeUtils;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public abstract class OutlinePage extends StackPane implements Page {

    protected static final int OUTLINE_WIDTH = 200;

    protected final ScrollPane scrollPane = new ScrollPane();
    protected final VBox userContent = new VBox();
    protected final StackPane userContentArea = new StackPane(userContent);
    protected final Outline outline = new Outline(createOutlineHandler());
    protected boolean isRendered = false;

    protected OutlinePage() {
        super();

        userContent.getStyleClass().add("user-content");
        getStyleClass().add("outline-page");

        createPageLayout();
    }

    protected void createPageLayout() {
        StackPane.setMargin(userContent, new Insets(0, OUTLINE_WIDTH, 0, 0));
        userContent.setMinWidth(Page.MAX_WIDTH - OUTLINE_WIDTH - 100);
        userContent.setMaxWidth(Page.MAX_WIDTH - OUTLINE_WIDTH - 100);

        scrollPane.setContent(userContentArea);
        NodeUtils.setScrollConstraints(scrollPane, AS_NEEDED, true, NEVER, true);
        scrollPane.setMaxHeight(20_000);

        // scroll spy
        scrollPane.vvalueProperty().addListener((obs, old, val) ->
            // we need a little gap between changing vValue and fetching header bounds
            Platform.runLater(() -> outline.select(getFirstVisibleHeader()))
        );

        var pageBody = new StackPane();
        pageBody.getChildren().setAll(scrollPane, outline);
        pageBody.getStyleClass().add("body");
        StackPane.setAlignment(outline, Pos.TOP_RIGHT);
        StackPane.setMargin(outline, new Insets(50, 20, 0, 0));

        setMinWidth(Page.MAX_WIDTH);
        getChildren().setAll(pageBody);
    }

    protected Consumer<Heading> createOutlineHandler() {
        return heading -> {
            if (!Objects.equals(heading, Heading.TOP)) {
                Parent container = heading.anchor().getParent();
                int indexInParent = container.getChildrenUnmodifiable().indexOf(heading.anchor());

                Node target;
                double targetY;

                if (container.getChildrenUnmodifiable().size() > indexInParent + 1) {
                    // aims to the middle of the content node (the one that is next to header)
                    target = container.getChildrenUnmodifiable().get(indexInParent + 1);
                    var bounds = target.getBoundsInParent();
                    targetY = bounds.getMaxY() - (bounds.getHeight() / 2);
                } else {
                    target = heading.anchor();
                    targetY = target.getBoundsInParent().getMaxY();
                }

                double height = scrollPane.getContent().getBoundsInLocal().getHeight();
                scrollPane.setVvalue(targetY / height);
            } else {
                scrollPane.setVvalue(0);
            }
        };
    }

    private @Nullable String getFirstVisibleHeader() {
        var scrollBounds = scrollPane.localToScene(scrollPane.getBoundsInParent());
        Label lastHeading = null;
        for (var node : userContent.getChildren()) {
            if (!(node instanceof Label heading)) {
                continue;
            }

            var headingBounds = heading.localToScene(heading.getBoundsInLocal());
            if (outline.contains(heading.getText())) {
                // viewport should fully contain heading bounds, not just a part of it
                if (scrollBounds.contains(headingBounds)) {
                    return heading.getText();
                } else {
                    lastHeading = heading;
                }
            }
        }

        return lastHeading != null ? lastHeading.getText() : null;
    }

    protected void addPageHeader() {
        var pageHeader = new PageHeader(this);
        userContent.getChildren().add(pageHeader);
    }

    protected void addNode(Node node) {
        userContent.getChildren().add(node);
    }

    protected void addFormattedText(String text) {
        addFormattedText(text, false);
    }

    protected void addFormattedText(String text, boolean handleUrl) {
        userContent.getChildren().add(createFormattedText(text, handleUrl));
    }

    protected void addSection(String title, Node content) {
        var titleIcon = new FontIcon(Feather.HASH);
        titleIcon.getStyleClass().add("icon-subtle");

        var titleLabel = new Label(title);
        titleLabel.getStyleClass().add(Styles.TITLE_3);
        titleLabel.setGraphic(titleIcon);
        titleLabel.setGraphicTextGap(10);
        titleLabel.setPadding(new Insets(20, 0, 0, 0));

        userContent.getChildren().addAll(titleLabel, content);
        outline.add(new Heading(title, titleLabel));
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

    ///////////////////////////////////////////////////////////////////////////

    public record Heading(String title, Node anchor) {

        private static final Heading TOP = new Heading("Top", new Text());

        public Heading {
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(anchor, "anchor");
        }
    }

    public static class Outline extends VBox {

        private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

        private final Set<String> toc = new LinkedHashSet<>();
        private final Consumer<Heading> clickHandler;
        private Label selected;

        public Outline(Consumer<Heading> clickHandler) {
            super();

            this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");

            // outline has two items by default at the bottom
            getChildren().add(new Separator());
            getChildren().add(createEntry(Heading.TOP));
            setPickOnBounds(false); // do not consume scroll events on transparent pixels

            getStyleClass().add("outline");
            setMinWidth(OUTLINE_WIDTH);
            setMaxWidth(OUTLINE_WIDTH);
        }

        public void add(Heading heading) {
            var label = createEntry(heading);
            if (getChildren().size() == 2) { // top entry
                label.pseudoClassStateChanged(SELECTED, true);
                this.selected = label;
            }

            Objects.requireNonNull(heading, "heading");
            getChildren().add(getChildren().size() - 2, label);
            toc.add(heading.title());
        }

        public boolean contains(String title) {
            return title != null && toc.contains(title);
        }

        public void select(String title) {
            if (selected != null) {
                selected.pseudoClassStateChanged(SELECTED, false);
            }

            for (var node : getChildren()) {
                if (node instanceof Label label && Objects.equals(label.getText(), title)) {
                    label.pseudoClassStateChanged(SELECTED, true);
                    selected = label;
                    break; // handle multiple entries share the same title
                }
            }
        }

        private Label createEntry(Heading heading) {
            var label = new Label(heading.title());
            label.setWrapText(true);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1 && e.getButton() == MouseButton.PRIMARY) {
                    clickHandler.accept(heading);
                }
            });
            return label;
        }
    }
}
