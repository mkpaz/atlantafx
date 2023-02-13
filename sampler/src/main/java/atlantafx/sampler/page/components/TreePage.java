/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.DENSE;
import static atlantafx.base.theme.Styles.toggleStyleClass;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.util.Arrays;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class TreePage extends AbstractPage {

    public static final String NAME = "TreeView";
    private static final int MAX_TREE_DEPTH = 3;
    private static final int[] TREE_DICE = {-1, 0, 1};

    @Override
    public String getName() {
        return NAME;
    }

    private final BorderPane treeWrapper = new BorderPane();
    private final ComboBox<Example> exampleSelect = createExampleSelect();

    public TreePage() {
        super();

        var sample = new SampleBlock("Playground", createPlayground());
        sample.setFillHeight(true);
        setUserContent(sample);
    }

    private VBox createPlayground() {
        var denseToggle = new ToggleSwitch("Dense");
        denseToggle.selectedProperty().addListener(
            (obs, old, value) -> findDisplayedTree().ifPresent(tv -> toggleStyleClass(tv, DENSE))
        );

        var showRootToggle = new ToggleSwitch("Show root");
        showRootToggle.selectedProperty().addListener((obs, old, val) -> findDisplayedTree().ifPresent(tv -> {
            if (val != null) {
                tv.setShowRoot(val);
            }
        }));
        showRootToggle.setSelected(true);

        var altIconToggle = new ToggleSwitch("Alt icon");
        altIconToggle.selectedProperty().addListener((obs, old, val) ->
            findDisplayedTree().ifPresent(tv -> toggleStyleClass(tv, Tweaks.ALT_ICON))
        );

        var edge2edgeToggle = new ToggleSwitch("Edge to edge");
        edge2edgeToggle.selectedProperty().addListener(
            (obs, old, val) -> findDisplayedTree().ifPresent(tv -> toggleStyleClass(tv, Tweaks.EDGE_TO_EDGE))
        );

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> findDisplayedTree().ifPresent(tv -> {
            if (val != null) {
                tv.setDisable(val);
            }
        }));

        var controls = new HBox(BLOCK_HGAP, denseToggle, showRootToggle, altIconToggle, edge2edgeToggle);
        controls.setAlignment(Pos.CENTER);

        VBox.setVgrow(treeWrapper, Priority.ALWAYS);

        var playground = new VBox(
            BLOCK_VGAP,
            new HBox(new Label("Select an example:"), new Spacer(), disableToggle),
            exampleSelect,
            treeWrapper,
            controls
        );
        playground.setMinHeight(100);

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

    private <T> void generateTree(TreeItem<T> parent, Supplier<TreeItem<T>> supplier, int limit, int depth) {
        if (limit == 0) {
            return;
        }

        var item = supplier.get();
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

        generateTree(nextParent, supplier, --limit, nextDepth);
    }

    private TreeView<String> stringTree() {
        var root = new TreeItem<>("root");
        root.setExpanded(true);

        var tree = new TreeView<String>();

        generateTree(root, () -> new TreeItem<>(FAKER.internet().domainWord()), 30, 1);
        tree.setRoot(root);

        return tree;
    }

    private TreeView<String> graphicTree() {
        var root = new TreeItem<>("root", new FontIcon(Feather.FOLDER));
        root.setExpanded(true);

        var tree = new TreeView<String>();

        generateTree(root, () -> new TreeItem<>(FAKER.internet().domainWord(), new FontIcon(Feather.FILE)), 30, 1);
        tree.setRoot(root);

        return tree;
    }

    private TreeView<String> editableTree() {
        var root = new TreeItem<>("root", new FontIcon(Feather.FOLDER));
        root.setExpanded(true);

        var tree = new TreeView<String>();
        tree.setCellFactory(TextFieldTreeCell.forTreeView());
        tree.setEditable(true);

        generateTree(root, () -> new TreeItem<>(FAKER.internet().domainWord(), new FontIcon(Feather.FILE)), 30, 1);
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

        generateTree(root, () -> new CheckBoxTreeItem<>(FAKER.internet().domainWord()), 30, 1);
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

        generateTree(root, () -> new TreeItem<>(FAKER.internet().domainWord()), 30, 1);
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

        generateTree(root, () -> new TreeItem<>(FAKER.internet().domainWord(), new FontIcon(Feather.FILE)), 30, 1);
        tree.setRoot(root);

        return tree;
    }

    ///////////////////////////////////////////////////////////////////////////

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
