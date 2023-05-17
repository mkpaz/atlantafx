/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.ChoiceBoxTreeCell;
import javafx.scene.control.cell.ComboBoxTreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class TreeViewPage extends OutlinePage {

    public static final String NAME = "TreeView";

    @Override
    public String getName() {
        return NAME;
    }

    public TreeViewPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]TreeView[/i] provides a way to present tree structures. A tree has \
            a root node which contains all the hierarchical values."""
        );
        addSection("Usage", usageExample());
        addSection("Dense", denseExample());
        addSection("Alt Icon", altIconExample());
        addSection("Edge-to-Edge", edge2EdgeExample());
        addSection("Playground", playground());
    }

    @SuppressWarnings("unchecked")
    private ExampleBox usageExample() {
        //snippet_1:start
        var item1 = new TreeItem<>("Programming", icon("code"));
        item1.getChildren().setAll(
            new TreeItem<>("Kotlin", icon("kotlin")),
            new TreeItem<>("Python", icon("python")),
            new TreeItem<>("C++", icon("cplusplus"))
        );

        var item2 = new TreeItem<>("Databases", icon("database"));
        item2.getChildren().setAll(
            new TreeItem<>("PostgreSQL", icon("postgresql")),
            new TreeItem<>("Redis", icon("redis")),
            new TreeItem<>("MongoDB", icon("mongodb"))
        );

        var item3 = new TreeItem<>("Cloud", icon("cloud"));
        item3.getChildren().setAll(
            new TreeItem<>("Terraform", icon("terraform")),
            new TreeItem<>("Azure", icon("azure")),
            new TreeItem<>("Kubernetes", icon("kubernetes"))
        );

        var root = new TreeItem<>("Technologies");
        root.setExpanded(true);
        root.getChildren().addAll(item1, item2, item3);

        var tree = new TreeView<>(root);
        //snippet_1:end

        tree.setMaxWidth(Double.MAX_VALUE);
        tree.setMinHeight(250);
        HBox.setHgrow(tree, Priority.ALWAYS);

        var box = new HBox(tree);
        var description = BBCodeParser.createFormattedText("""
            In each tree the highest object in the hierarchy is called the "root". \
            The root contains several child items, which can have children as well. \
            An item without children is called "leaf".""");

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox denseExample() {
        //snippet_2:start
        class Helper {
            public static TreeItem<String> scan(File dir, int depth) {
                var parent = new TreeItem<>(
                    dir.getName(),
                    new FontIcon(Feather.FOLDER)
                );
                File[] files = dir.listFiles();
                depth--;

                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory() && depth > 0) {
                            parent.getChildren().add(scan(f, depth));
                        } else {
                            var leaf = new TreeItem<>(
                                f.getName(),
                                new FontIcon(Feather.FILE)
                            );
                            parent.getChildren().add(leaf);
                        }
                    }
                    parent.getChildren().sort(
                        Comparator.comparing(TreeItem::getValue)
                    );
                }

                return parent;
            }
        }

        var root = Helper.scan(new File(System.getProperty("user.home")), 3);

        var tree = new TreeView<>(root);
        tree.getStyleClass().add(Styles.DENSE);
        tree.setShowRoot(false);
        //snippet_2:end

        tree.setMaxWidth(Double.MAX_VALUE);
        tree.setMinHeight(350);
        HBox.setHgrow(tree, Priority.ALWAYS);

        var box = new HBox(tree);
        var description = BBCodeParser.createFormattedText(
            "The [i]TreeView[/i] rows can be made more compact by cutting tree cell padding."
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox altIconExample() {
        //snippet_3:start
        var item1 = new TreeItem<>("Algorithms");
        item1.getChildren().setAll(
            new TreeItem<>("Bubble Sort"),
            new TreeItem<>("Selection Sort"),
            new TreeItem<>("Merge Sort")
        );

        var item2 = new TreeItem<>("Data Structures");
        item2.getChildren().setAll(
            new TreeItem<>("Stack"),
            new TreeItem<>("Queue"),
            new TreeItem<>("Linked List")
        );

        var item3 = new TreeItem<>("Artificial Intelligence");
        item3.getChildren().setAll(
            new TreeItem<>("Machine Learning"),
            new TreeItem<>("Natural Language Processing"),
            new TreeItem<>("Robotics")
        );

        var root = new TreeItem<>("Computer Science");
        root.setExpanded(true);
        root.getChildren().addAll(item1, item2, item3);

        var tree = new TreeView<>(root);
        tree.getStyleClass().add(Tweaks.ALT_ICON);
        //snippet_3:end

        tree.setMaxWidth(Double.MAX_VALUE);
        tree.setMinHeight(250);
        HBox.setHgrow(tree, Priority.ALWAYS);

        var box = new HBox(tree);
        var description = BBCodeParser.createFormattedText("""
            There's additional tweak [code]Tweaks.ALT_ICON[/code] to change the [i]TreeView[/i]  \
            arrow icon."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox edge2EdgeExample() {
        //snippet_4:start
        var item1 = new TreeItem<>("Action");
        item1.getChildren().setAll(
            new TreeItem<>("Grand Theft Auto V"),
            new TreeItem<>("Call of Duty: Modern Warfare"),
            new TreeItem<>("Uncharted 4: A Thief's End")
        );

        var item2 = new TreeItem<>("Adventure");
        item2.getChildren().setAll(
            new TreeItem<>("The Legend of Zelda: Breath of the Wild"),
            new TreeItem<>("Tomb Raider"),
            new TreeItem<>("Minecraft")
        );

        var item3 = new TreeItem<>("Role-playing");
        item3.getChildren().setAll(
            new TreeItem<>("The Elder Scrolls V: Skyrim"),
            new TreeItem<>("Fallout 4"),
            new TreeItem<>("Final Fantasy XV")
        );

        var root = new TreeItem<>("Games");
        root.setExpanded(true);
        root.getChildren().addAll(item1, item2, item3);

        var tree = new TreeView<>(root);
        //snippet_4:end

        tree.setMaxWidth(Double.MAX_VALUE);
        tree.setMinHeight(250);
        HBox.setHgrow(tree, Priority.ALWAYS);

        var box = new HBox(tree);
        box.setStyle("""
            -fx-border-color: -color-accent-emphasis;
            -fx-border-width: 2px;"""
        );

        var description = BBCodeParser.createFormattedText("""
            Use [code]Tweaks.EDGE_TO_EDGE[/code] style class to remove the [i]TreeView[/i] outer borders. \
            This is useful if you want to place the table into external container that already has its \
            own borders."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    private ImageView icon(String name) {
        var img = new Image(Resources.getResourceAsStream("images/devicons/" + name + ".png"));
        return new ImageView(img);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private static final int MAX_TREE_DEPTH = 3;
    private static final int[] TREE_DICE = {-1, 0, 1};

    private final BorderPane treeWrapper = new BorderPane();
    private final ComboBox<Example> exampleSelect = createExampleSelect();

    private VBox playground() {
        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener((obs, old, value) ->
            findDisplayedTree().ifPresent(tv -> Styles.toggleStyleClass(tv, Styles.DENSE))
        );

        var showRootToggle = new ToggleSwitch("Show root");
        showRootToggle.selectedProperty().addListener((obs, old, val) ->
            findDisplayedTree().ifPresent(tv -> {
                if (val != null) {
                    tv.setShowRoot(val);
                }
            })
        );
        showRootToggle.setSelected(true);

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty().addListener((obs, old, val) ->
            findDisplayedTree().ifPresent(tv -> Styles.toggleStyleClass(tv, Tweaks.ALT_ICON))
        );

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener((obs, old, val) ->
            findDisplayedTree().ifPresent(tv -> Styles.toggleStyleClass(tv, Tweaks.EDGE_TO_EDGE))
        );

        var toggles = new HBox(VGAP_20, denseToggle, showRootToggle, altIconToggle, edge2edgeToggle);
        toggles.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]TreeView[/i] features \
            and also serves as an object for monkey testing."""
        );

        VBox.setVgrow(treeWrapper, Priority.ALWAYS);

        var playground = new VBox(
            VGAP_10,
            description,
            new Label("Select an example:"),
            exampleSelect,
            treeWrapper,
            toggles
        );
        playground.setMinHeight(500);

        return playground;
    }

    private ComboBox<Example> createExampleSelect() {
        var select = new ComboBox<Example>();
        select.setMaxWidth(Double.MAX_VALUE);
        select.getItems().setAll(Example.values());

        select.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }

            TreeView<String> newTree = createTree(val);

            // copy existing style classes and properties to the new tree
            findDisplayedTree().ifPresent(tv -> {
                List<String> currentStyles = tv.getStyleClass();
                currentStyles.remove("tree-view");
                newTree.getStyleClass().addAll(currentStyles);
                newTree.setShowRoot(tv.isShowRoot());
                newTree.setDisable(tv.isDisable());
            });

            treeWrapper.setCenter(newTree);
        });

        select.setConverter(new StringConverter<>() {
            @Override
            public String toString(Example example) {
                return example == null ? "" : example.getName();
            }

            @Override
            public Example fromString(String s) {
                return Example.find(s);
            }
        });

        return select;
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        exampleSelect.getSelectionModel().selectFirst();
    }

    private Optional<TreeView<?>> findDisplayedTree() {
        return treeWrapper.getChildren().size() > 0
            ? Optional.of((TreeView<?>) treeWrapper.getChildren().get(0))
            : Optional.empty();
    }

    private TreeView<String> createTree(Example example) {
        switch (example) {
            case TEXT -> {
                return stringTree();
            }
            case GRAPHIC -> {
                return graphicTree();
            }
            case EDITABLE -> {
                return editableTree();
            }
            case CHECK_BOX -> {
                return checkBoxTree();
            }
            case CHOICE_BOX -> {
                return choiceBoxTree();
            }
            case COMBO_BOX -> {
                return comboBoxTree();
            }
            default -> throw new IllegalArgumentException("Unexpected enum value: " + example);
        }
    }

    private <T> void generateTree(TreeItem<T> parent,
                                  Supplier<TreeItem<T>> itemFactory,
                                  int childLimit,
                                  int depth) {
        if (childLimit == 0) {
            return;
        }

        var item = itemFactory.get();
        parent.getChildren().add(item);

        TreeItem<T> nextParent = parent; // sibling
        int nextDepth = depth;
        int rand = TREE_DICE[RANDOM.nextInt(TREE_DICE.length)];

        if (rand < 0 && parent.getParent() != null) { // go up
            nextParent = parent.getParent();
            nextDepth = --depth;
        }
        if (rand > 0 && depth < MAX_TREE_DEPTH) { // go down
            nextParent = item;
            nextDepth = ++depth;
        }

        generateTree(nextParent, itemFactory, --childLimit, nextDepth);
    }

    private TreeView<String> stringTree() {
        var root = new TreeItem<>("root");
        root.setExpanded(true);

        var tree = new TreeView<String>();

        generateTree(root, () -> new TreeItem<>(
            FAKER.internet().domainWord()), 30, 1
        );
        tree.setRoot(root);

        return tree;
    }

    private TreeView<String> graphicTree() {
        var root = new TreeItem<>("root", new FontIcon(Feather.FOLDER));
        root.setExpanded(true);

        var tree = new TreeView<String>();

        generateTree(root, () -> new TreeItem<>(
            FAKER.internet().domainWord(), new FontIcon(Feather.FILE)), 30, 1
        );
        tree.setRoot(root);

        return tree;
    }

    private TreeView<String> editableTree() {
        var root = new TreeItem<>("root", new FontIcon(Feather.FOLDER));
        root.setExpanded(true);

        var tree = new TreeView<String>();
        tree.setCellFactory(TextFieldTreeCell.forTreeView());
        tree.setEditable(true);

        generateTree(root, () -> new TreeItem<>(
            FAKER.internet().domainWord(), new FontIcon(Feather.FILE)), 30, 1
        );
        tree.setRoot(root);

        return tree;
    }

    // Note that CheckBoxTreeCell is incompatible with user graphic,
    // because it adds graphic inside .checkbox container. #javafx-bug
    private TreeView<String> checkBoxTree() {
        var root = new CheckBoxTreeItem<>("root");
        root.setExpanded(true);

        var tree = new TreeView<String>();
        tree.setCellFactory(CheckBoxTreeCell.forTreeView());

        generateTree(root, () -> new CheckBoxTreeItem<>(
            FAKER.internet().domainWord()), 30, 1
        );
        tree.setRoot(root);

        return tree;
    }

    private TreeView<String> choiceBoxTree() {
        var root = new TreeItem<>("root");
        root.setExpanded(true);

        var tree = new TreeView<String>();
        tree.setCellFactory(ChoiceBoxTreeCell.forTreeView(
            generate(() -> FAKER.internet().domainWord(), 10).toArray(String[]::new)
        ));
        tree.setEditable(true);

        generateTree(root, () -> new TreeItem<>(
            FAKER.internet().domainWord()), 30, 1
        );
        tree.setRoot(root);

        return tree;
    }

    private TreeView<String> comboBoxTree() {
        var root = new TreeItem<>("root", new FontIcon(Feather.FOLDER));
        root.setExpanded(true);

        var tree = new TreeView<String>();
        tree.setCellFactory(ComboBoxTreeCell.forTreeView(
            generate(() -> FAKER.internet().domainWord(), 10).toArray(String[]::new)
        ));
        tree.setEditable(true);

        generateTree(root, () -> new TreeItem<>(
            FAKER.internet().domainWord(), new FontIcon(Feather.FILE)), 30, 1
        );
        tree.setRoot(root);

        return tree;
    }

    private enum Example {

        TEXT("Text"),
        GRAPHIC("Text with icons"),
        EDITABLE("TextFieldTreeCell"),
        CHECK_BOX("CheckBoxTreeCell"),
        CHOICE_BOX("ChoiceBoxTreeCell"),
        COMBO_BOX("ComboBoxTreeCell");

        private final String name;

        Example(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Example find(String name) {
            return Arrays.stream(Example.values())
                .filter(example -> Objects.equals(example.getName(), name))
                .findFirst()
                .orElse(null);
        }
    }
}
