/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.filemanager;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.showcase.ShowcasePage;
import atlantafx.sampler.util.Containers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.sampler.page.showcase.filemanager.FileList.PREDICATE_ANY;
import static atlantafx.sampler.page.showcase.filemanager.FileList.PREDICATE_NOT_HIDDEN;
import static atlantafx.sampler.page.showcase.filemanager.Utils.openFile;

public class FileManagerPage extends ShowcasePage {

    public static final String NAME = "File Manager";

    private static final String STYLESHEET_URL =
            Objects.requireNonNull(FileManagerPage.class.getResource("file-manager.css")).toExternalForm();

    @Override
    public String getName() { return NAME; }

    private final Model model = new Model();

    public FileManagerPage() {
        super();
        createView();
    }

    private void createView() {
        var topBar = new ToolBar();

        var backBtn = new Button("", new FontIcon(Feather.ARROW_LEFT));
        backBtn.getStyleClass().addAll(BUTTON_ICON, LEFT_PILL);
        backBtn.setOnAction(e -> model.back());
        backBtn.disableProperty().bind(model.getHistory().canGoBackProperty().not());

        var forthBtn = new Button("", new FontIcon(Feather.ARROW_RIGHT));
        forthBtn.getStyleClass().addAll(BUTTON_ICON, RIGHT_PILL);
        forthBtn.setOnAction(e -> model.forth());
        forthBtn.disableProperty().bind(model.getHistory().canGoForthProperty().not());

        var backForthBox = new HBox(backBtn, forthBtn);
        backForthBox.setAlignment(Pos.CENTER_LEFT);

        Breadcrumbs<Path> breadcrumbs = breadCrumbs();
        breadcrumbs.setOnCrumbAction(e -> model.navigate(e.getSelectedCrumb().getValue(), true));

        var toggleHiddenCheck = new CheckMenuItem("Show Hidden Files");
        toggleHiddenCheck.setSelected(true);

        var menuBtn = new MenuButton();
        menuBtn.setGraphic(new FontIcon(Feather.MORE_HORIZONTAL));
        menuBtn.getItems().setAll(
                toggleHiddenCheck
        );
        menuBtn.getStyleClass().add(BUTTON_ICON);

        topBar.getItems().setAll(
                backForthBox,
                breadcrumbs,
                new Spacer(),
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
        splitPane.setDividerPositions(0.2, 0.8);

        var root = new BorderPane();
        root.getStyleClass().add("file-manager-showcase");
        root.getStylesheets().add(STYLESHEET_URL);
        root.setTop(topBar);
        root.setCenter(splitPane);

        toggleHiddenCheck.selectedProperty().addListener((obs, old, val) -> directoryView.getFileList()
                .predicateProperty()
                .set(val ? PREDICATE_ANY : PREDICATE_NOT_HIDDEN)
        );

        model.currentPathProperty().addListener((obs, old, val) -> {
            if (!Files.isReadable(val)) {
                showWarning("Access Denied", "You have no permission to enter \"" + val + "\"");
                return;
            }

            breadcrumbs.setSelectedCrumb(
                    Breadcrumbs.buildTreeModel(getParentPath(val, 5).toArray(Path[]::new))
            );
            directoryView.setDirectory(val);
        });

        showcase.getChildren().setAll(root);
        Containers.setAnchors(root, Insets.EMPTY);
    }

    private Breadcrumbs<Path> breadCrumbs() {
        Callback<TreeItem<Path>, Button> crumbFactory = crumb -> {
            var btn = new Button(crumb.getValue().getFileName().toString());
            btn.getStyleClass().add(Styles.FLAT);
            btn.setFocusTraversable(false);
            if (!crumb.getChildren().isEmpty()) {
                btn.setGraphic(new FontIcon(Feather.CHEVRON_RIGHT));
            }
            btn.setContentDisplay(ContentDisplay.RIGHT);
            btn.setStyle("-fx-padding: 6px 12px 6px 12px;");
            return btn;
        };

        var breadcrumbs = new Breadcrumbs<Path>();
        breadcrumbs.setAutoNavigationEnabled(false);
        breadcrumbs.setCrumbFactory(crumbFactory);
        breadcrumbs.setSelectedCrumb(
                Breadcrumbs.buildTreeModel(getParentPath(model.currentPathProperty().get(), 5).toArray(Path[]::new))
        );
        breadcrumbs.setMaxWidth(Region.USE_PREF_SIZE);
        breadcrumbs.setMaxHeight(Region.USE_PREF_SIZE);

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
