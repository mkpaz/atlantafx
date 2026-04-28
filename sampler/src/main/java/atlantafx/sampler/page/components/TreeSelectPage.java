/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.TreeSelect;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

public class TreeSelectPage extends OutlinePage {

    public static final String NAME = "TreeSelect";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public TreeSelectPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]TreeSelect[/i] control wraps a [code]TreeView[/code] inside a \
            dropdown popup. It supports both single and multiple selection modes \
            via the [code]selectionMode[/code] property."""
        );
        addSection("Single Selection", singleExample());
        addSection("Multiple Selection", multiExample());
        addSection("Custom Tree", customExample());
    }

    private Node singleExample() {
        //snippet_1:start
        var root = new TreeItem<String>("Company");
        root.setExpanded(true);

        var dept1 = new TreeItem<>("Engineering");
        dept1.getChildren().setAll(
            new TreeItem<>("Frontend"),
            new TreeItem<>("Backend"),
            new TreeItem<>("DevOps")
        );

        var dept2 = new TreeItem<>("Design");
        dept2.getChildren().setAll(
            new TreeItem<>("UI/UX"),
            new TreeItem<>("Graphic")
        );

        root.getChildren().setAll(dept1, dept2);

        var select = new TreeSelect<>(root);
        select.setPromptText("Select department...");
        //snippet_1:end

        var box = new VBox(10, select);
        var description = BBCodeParser.createFormattedText("""
            Single selection mode (default). Clicking a tree node selects it \
            and closes the dropdown."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node multiExample() {
        //snippet_2:start
        var root = new TreeItem<String>("Categories");
        root.setExpanded(true);

        var lang = new TreeItem<>("Languages");
        lang.getChildren().setAll(
            new TreeItem<>("Java"),
            new TreeItem<>("Kotlin"),
            new TreeItem<>("Python")
        );

        var framework = new TreeItem<>("Frameworks");
        framework.getChildren().setAll(
            new TreeItem<>("Spring"),
            new TreeItem<>("Jakarta EE")
        );

        root.getChildren().setAll(lang, framework);

        var select = new TreeSelect<>(root);
        select.setPromptText("Select categories...");
        select.setSelectionMode(SelectionMode.MULTIPLE);
        //snippet_2:end

        var box = new VBox(10, select);
        var description = BBCodeParser.createFormattedText("""
            Set [code]selectionMode[/code] to [code]SelectionMode.MULTIPLE[/code] \
            to enable multi-selection with checkboxes. The trigger displays a \
            count of selected items."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node customExample() {
        //snippet_3:start
        var root = new TreeItem<String>("Root");
        for (int i = 1; i <= 5; i++) {
            var parent = new TreeItem<>("Item " + i);
            for (int j = 1; j <= 3; j++) {
                parent.getChildren().add(new TreeItem<>("  Sub " + i + "." + j));
            }
            root.getChildren().add(parent);
        }

        var select = new TreeSelect<>(root);
        select.setShowRoot(false);
        select.setPromptText("Choose an item...");
        //snippet_3:end

        var box = new VBox(10, select);
        var description = BBCodeParser.createFormattedText("""
            Set [code]showRoot[/code] to [code]false[/code] to hide the root item \
            and show only its children."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }
}
