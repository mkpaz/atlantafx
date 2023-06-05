/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static atlantafx.sampler.page.showcase.filemanager.FileList.PREDICATE_ANY;
import static atlantafx.sampler.page.showcase.filemanager.FileList.PREDICATE_NOT_HIDDEN;
import static atlantafx.sampler.page.showcase.filemanager.Utils.openFile;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.Breadcrumbs.BreadCrumbItem;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.showcase.ShowcasePage;
import atlantafx.sampler.util.NodeUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public class FileManagerPage extends ShowcasePage {

    public static final String NAME = "File Manager";

    private static final String STYLESHEET_URL =
        Objects.requireNonNull(FileManagerPage.class.getResource("file-manager.css")).toExternalForm();

    @Override
    public String getName() {
        return NAME;
    }

    private final Model model = new Model();

    public FileManagerPage() {
        super();
        createView();
    }

    private void createView() {
        var backBtn = new Button(null, new FontIcon(Feather.ARROW_LEFT));
        backBtn.getStyleClass().add(Styles.BUTTON_ICON);
        backBtn.setOnAction(e -> model.back());
        backBtn.disableProperty().bind(model.getHistory().canGoBackProperty().not());
        backBtn.setTooltip(new Tooltip("Back"));

        var forthBtn = new Button(null, new FontIcon(Feather.ARROW_RIGHT));
        forthBtn.getStyleClass().add(Styles.BUTTON_ICON);
        forthBtn.setOnAction(e -> model.forth());
        forthBtn.disableProperty().bind(model.getHistory().canGoForthProperty().not());
        forthBtn.setTooltip(new Tooltip("Forward"));

        Breadcrumbs<Path> breadcrumbs = createBreadcrumbs();
        breadcrumbs.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(breadcrumbs, Priority.ALWAYS);
        breadcrumbs.setOnCrumbAction(e -> model.navigate(e.getSelectedCrumb().getValue(), true));

        var createBtn = new Button(null, new FontIcon(Feather.FOLDER));
        createBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        createBtn.getStyleClass().add(Styles.BUTTON_ICON);

        var treeTgl = new ToggleButton("", new FontIcon(Material2OutlinedAL.ACCOUNT_TREE));
        treeTgl.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        treeTgl.getStyleClass().add(Styles.BUTTON_ICON);
        treeTgl.setSelected(true);
        treeTgl.setTooltip(new Tooltip("Show tree"));

        var hiddenTgl = new ToggleButton("", new FontIcon(Material2OutlinedAL.FILTER_LIST));
        hiddenTgl.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        hiddenTgl.getStyleClass().add(Styles.BUTTON_ICON);
        hiddenTgl.setSelected(true);
        hiddenTgl.setTooltip(new Tooltip("Show hidden files"));

        var topBar = new ToolBar();
        topBar.getItems().setAll(
            backBtn,
            forthBtn,
            new Spacer(10),
            breadcrumbs,
            new Spacer(10),
            treeTgl,
            hiddenTgl
        );

        // ~
        var dirTree = new DirectoryTree(model, Model.USER_HOME.toFile());
        dirTree.setMinWidth(100);

        var dirView = new TableDirectoryView();
        dirView.setMinWidth(300);
        dirView.setDirectory(model.currentPathProperty().get());
        dirView.setOnAction(path -> {
            if (Files.isDirectory(path)) {
                model.navigate(path, true);
            } else {
                openFile(path);
            }
        });

        // ~
        var splitPane = new SplitPane(dirTree, dirView.getView());
        splitPane.widthProperty().addListener(
            // set sidebar width in pixels depending on split pane width
            (obs, old, val) -> splitPane.setDividerPosition(0, 300 / splitPane.getWidth())
        );

        var root = new BorderPane();
        root.setId("file-manager-showcase");
        root.getStylesheets().add(STYLESHEET_URL);
        root.setTop(new VBox(topBar));
        root.setCenter(splitPane);

        hiddenTgl.selectedProperty().addListener((obs, old, val) -> dirView.getFileList()
            .predicateProperty()
            .set(!val ? PREDICATE_ANY : PREDICATE_NOT_HIDDEN)
        );
        dirView.getFileList().predicateProperty().set(PREDICATE_NOT_HIDDEN);

        treeTgl.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                splitPane.getItems().add(0, dirTree);
                splitPane.setDividerPosition(0, 300 / splitPane.getWidth());
            } else {
                splitPane.getItems().remove(dirTree);
            }
        });

        model.currentPathProperty().addListener((obs, old, val) -> {
            if (!Files.isReadable(val)) {
                showWarning("Access Denied", "You have no permission to enter \"" + val + "\"");
                return;
            }

            // crumb count should be calculated depending on available width
            breadcrumbs.setSelectedCrumb(
                Breadcrumbs.buildTreeModel(getParentPath(val, 4).toArray(Path[]::new))
            );
            dirView.setDirectory(val);
        });

        setWindowTitle("File Manager", new FontIcon(Material2AL.FOLDER));
        setAboutInfo("""
            This is a simple file manager. You can navigate through \
            the file system and open files using the default system application."""
        );
        setShowCaseContent(root, MAX_WIDTH, 600);

        NodeUtils.setAnchors(root, Insets.EMPTY);
    }

    private Breadcrumbs<Path> createBreadcrumbs() {
        Callback<BreadCrumbItem<Path>, ButtonBase> crumbFactory = crumb -> {
            var hyperlink = new Hyperlink(crumb.getValue().getFileName().toString());
            hyperlink.setFocusTraversable(false);
            return hyperlink;
        };

        Callback<BreadCrumbItem<Path>, ? extends Node> dividerFactory = item ->
            item != null && !item.isLast()
                ? new Label("", new FontIcon(Material2AL.CHEVRON_RIGHT))
                : null;

        var breadcrumbs = new Breadcrumbs<Path>();
        breadcrumbs.setAutoNavigationEnabled(false);
        breadcrumbs.setCrumbFactory(crumbFactory);
        breadcrumbs.setDividerFactory(dividerFactory);
        breadcrumbs.setSelectedCrumb(Breadcrumbs.buildTreeModel(
            getParentPath(model.currentPathProperty().get(), 4).toArray(Path[]::new))
        );

        return breadcrumbs;
    }

    @SuppressWarnings("SameParameterValue")
    private List<Path> getParentPath(Path path, int limit) {
        var result = new ArrayList<Path>();

        var cursor = path;
        while (result.size() < limit && cursor.getParent() != null) {
            result.add(cursor);
            cursor = cursor.getParent();
        }
        Collections.reverse(result);

        return result;
    }
}
