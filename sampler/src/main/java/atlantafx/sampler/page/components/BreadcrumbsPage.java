/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.Breadcrumbs.BreadCrumbItem;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public final class BreadcrumbsPage extends OutlinePage {

    public static final String NAME = "Breadcrumbs";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public BreadcrumbsPage() {
        super();

        addPageHeader();
        addFormattedText("""
            Represents a bread crumb bar. This control is useful to visualize and navigate \
            a hierarchical path structure, such as file systems."""
        );
        addSection("Usage", usageExample());
        addSection("Custom Item", customItemExample());
        addSection("Custom Divider", customDividerExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var items = generate(() -> FAKER.science().element(), 4);
        BreadCrumbItem<String> root = Breadcrumbs.buildTreeModel(
            items.toArray(String[]::new)
        );

        var crumbs = new Breadcrumbs<>(root);
        crumbs.setSelectedCrumb(getTreeItemByIndex(root, 2));
        //snippet_1:end

        var nextBtn = new Button("Next");
        nextBtn.getStyleClass().addAll(Styles.ACCENT);
        nextBtn.setOnAction(e -> {
            var selected = crumbs.getSelectedCrumb();
            if (selected.getChildren().size() > 0) {
                var next = selected.getChildren().get(0);
                crumbs.setSelectedCrumb((BreadCrumbItem<String>) next);
            }
        });

        crumbs.selectedCrumbProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            nextBtn.setDisable(val.getChildren().isEmpty());
        });

        var box = new HBox(40, nextBtn, crumbs);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            The [i]BreadCrumbs[/i] uses the [i]TreeItem[/i] API to maintain its model. \
            You can create a tree model from a flat list by using the static \
            [code]buildTreeModel()[/code] method."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox customItemExample() {
        //snippet_2:start
        var items = generate(() -> FAKER.science().element(), 4);
        BreadCrumbItem<String> root = Breadcrumbs.buildTreeModel(
            items.toArray(String[]::new)
        );

        var crumbs = new Breadcrumbs<>(root);
        crumbs.setCrumbFactory(crumb -> {
            var btn = new Button(crumb.getValue(), new FontIcon(randomIcon()));
            btn.getStyleClass().add(Styles.FLAT);
            btn.setFocusTraversable(false);
            return btn;
        });
        crumbs.setSelectedCrumb(getTreeItemByIndex(root, 2));
        //snippet_2:end

        var nextBtn = new Button("Next");
        nextBtn.getStyleClass().addAll(Styles.ACCENT);
        nextBtn.setOnAction(e -> {
            var selected = crumbs.getSelectedCrumb();
            if (selected.getChildren().size() > 0) {
                var next = selected.getChildren().get(0);
                crumbs.setSelectedCrumb((BreadCrumbItem<String>) next);
            }
        });

        crumbs.selectedCrumbProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            nextBtn.setDisable(val.getChildren().isEmpty());
        });

        var box = new HBox(40, nextBtn, crumbs);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            By default, the [i]Breadcrumbs[/i] uses [i]Hyperlink[/i] to represent \
            its items. If you want to use a different kind of item, you can provide \
            a [code]crumbFactory[/code] to insert an arbitrary [i]Node[/i]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox customDividerExample() {
        //snippet_3:start
        var items = generate(() -> FAKER.science().element(), 4);
        BreadCrumbItem<String> root = Breadcrumbs.buildTreeModel(
            items.toArray(String[]::new)
        );

        var crumbs = new Breadcrumbs<>(root);
        crumbs.setDividerFactory(item -> {
            if (item == null) {
                return new Label("", new FontIcon(Material2AL.HOME));
            }
            return !item.isLast()
                ? new Label("", new FontIcon(Material2AL.CHEVRON_RIGHT))
                : null;
        });
        crumbs.setSelectedCrumb(getTreeItemByIndex(root, 2));
        //snippet_3:end

        var nextBtn = new Button("Next");
        nextBtn.getStyleClass().addAll(Styles.ACCENT);
        nextBtn.setOnAction(e -> {
            var selected = crumbs.getSelectedCrumb();
            if (selected.getChildren().size() > 0) {
                var next = selected.getChildren().get(0);
                crumbs.setSelectedCrumb((BreadCrumbItem<String>) next);
            }
        });

        crumbs.selectedCrumbProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            nextBtn.setDisable(val.getChildren().isEmpty());
        });

        var box = new HBox(40, nextBtn, crumbs);
        box.setAlignment(Pos.CENTER_LEFT);

        var description = BBCodeParser.createFormattedText("""
            Similarly, you can customize the [i]Breadcrumbs[/i] divider by \
            providing a [code]dividerFactory[/code]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private <T> BreadCrumbItem<T> getTreeItemByIndex(BreadCrumbItem<T> node, int index) {
        var counter = index;
        var current = node;
        while (counter > 0 && current.getParent() != null) {
            current = (BreadCrumbItem<T>) current.getParent();
            counter--;
        }
        return current;
    }
}
