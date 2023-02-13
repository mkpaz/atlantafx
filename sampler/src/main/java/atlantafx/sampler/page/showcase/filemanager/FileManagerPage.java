/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.sampler.page.showcase.filemanager.FileList.PREDICATE_ANY;
import static atlantafx.sampler.page.showcase.filemanager.FileList.PREDICATE_NOT_HIDDEN;
import static atlantafx.sampler.page.showcase.filemanager.Utils.openFile;
import static atlantafx.sampler.util.Controls.hyperlink;
import static atlantafx.sampler.util.Controls.iconButton;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.Breadcrumbs.BreadCrumbItem;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.showcase.ShowcasePage;
import atlantafx.sampler.util.Containers;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

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
        var backBtn = iconButton(Feather.ARROW_LEFT, false);
        backBtn.setOnAction(e -> model.back());
        backBtn.disableProperty().bind(model.getHistory().canGoBackProperty().not());

        var forthBtn = iconButton(Feather.ARROW_RIGHT, false);
        forthBtn.setOnAction(e -> model.forth());
        forthBtn.disableProperty().bind(model.getHistory().canGoForthProperty().not());

        Breadcrumbs<Path> breadcrumbs = breadCrumbs();
        breadcrumbs.setOnCrumbAction(e -> model.navigate(e.getSelectedCrumb().getValue(), true));

        var createMenuBtn = new MenuButton();
        createMenuBtn.setText("New");
        createMenuBtn.getItems().setAll(
            new MenuItem("New Folder"),
            new MenuItem("New Document")
        );

        var toggleHiddenCheck = new CheckMenuItem("Show Hidden Files");
        toggleHiddenCheck.setSelected(false);

        var menuBtn = new MenuButton();
        menuBtn.setGraphic(new FontIcon(Material2MZ.MENU));
        menuBtn.getItems().setAll(toggleHiddenCheck);
        menuBtn.getStyleClass().addAll(BUTTON_ICON, Tweaks.NO_ARROW);

        var topBar = new ToolBar();
        topBar.getItems().setAll(
            backBtn,
            forthBtn,
            new Spacer(10),
            breadcrumbs,
            new Spacer(),
            createMenuBtn,
            menuBtn
        );

        // ~

        BookmarkList bookmarksList = new BookmarkList(model);

        DirectoryView directoryView = new TableDirectoryView();
        directoryView.setDirectory(model.currentPathProperty().get());
        directoryView.setOnAction(path -> {
            if (Files.isDirectory(path)) {
                model.navigate(path, true);
            } else {
                openFile(path);
            }
        });

        // ~

        var splitPane = new SplitPane(bookmarksList, directoryView.getView());
        splitPane.widthProperty().addListener(
            // set sidebar width in pixels depending on split pane width
            (obs, old, val) -> splitPane.setDividerPosition(0, 200 / splitPane.getWidth())
        );

        var aboutBox = new HBox(new Text(
            "Simple file manager. You can traverse through the file system and open files"
                + " via default system application."
        ));
        aboutBox.setPadding(new Insets(5, 0, 5, 0));
        aboutBox.getStyleClass().add("about");

        var creditsBox = new HBox(5,
            new Text("Inspired by ©"),
            hyperlink("Gnome Files", URI.create("https://gitlab.gnome.org/GNOME/nautilus"))
        );
        creditsBox.getStyleClass().add("credits");
        creditsBox.setAlignment(Pos.CENTER_RIGHT);
        creditsBox.setPadding(new Insets(5, 0, 5, 0));

        var root = new BorderPane();
        root.getStyleClass().add("file-manager-showcase");
        root.getStylesheets().add(STYLESHEET_URL);
        root.setTop(new VBox(aboutBox, topBar));
        root.setCenter(splitPane);
        root.setBottom(creditsBox);

        toggleHiddenCheck.selectedProperty().addListener((obs, old, val) -> directoryView.getFileList()
            .predicateProperty()
            .set(val ? PREDICATE_ANY : PREDICATE_NOT_HIDDEN)
        );
        directoryView.getFileList().predicateProperty().set(PREDICATE_NOT_HIDDEN);

        model.currentPathProperty().addListener((obs, old, val) -> {
            if (!Files.isReadable(val)) {
                showWarning("Access Denied", "You have no permission to enter \"" + val + "\"");
                return;
            }

            // crumb count should be calculated depending on available width
            breadcrumbs.setSelectedCrumb(
                Breadcrumbs.buildTreeModel(getParentPath(val, 4).toArray(Path[]::new))
            );
            directoryView.setDirectory(val);
        });

        showcase.getChildren().setAll(root);
        Containers.setAnchors(root, Insets.EMPTY);
    }

    private Breadcrumbs<Path> breadCrumbs() {
        Callback<BreadCrumbItem<Path>, ButtonBase> crumbFactory = crumb -> {
            var btn = new Button(crumb.getValue().getFileName().toString());
            btn.getStyleClass().add(FLAT);
            btn.setFocusTraversable(false);
            return btn;
        };

        Callback<BreadCrumbItem<Path>, ? extends Node> dividerFactory = item ->
            item != null && !item.isLast() ? new Label("", new FontIcon(Material2AL.CHEVRON_RIGHT)) : null;

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
