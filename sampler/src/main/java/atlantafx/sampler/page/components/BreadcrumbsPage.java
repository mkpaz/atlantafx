/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class BreadcrumbsPage extends AbstractPage {

    public static final String NAME = "Breadcrumbs";

    @Override
    public String getName() { return NAME; }

    public BreadcrumbsPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(
                defaultSample().getRoot(),
                customCrumbSample().getRoot()
        );
    }

    private SampleBlock defaultSample() {
        return new SampleBlock("Basic", breadcrumbs(null));
    }

    private SampleBlock customCrumbSample() {
        Callback<TreeItem<String>, Button> crumbFactory = crumb -> {
            var btn = new Button(crumb.getValue());
            btn.getStyleClass().add(Styles.FLAT);
            btn.setFocusTraversable(false);
            if (!crumb.getChildren().isEmpty()) {
                btn.setGraphic(new FontIcon(Feather.CHEVRON_RIGHT));
            }
            return btn;
        };

        return new SampleBlock("Custom crumb factory", breadcrumbs(crumbFactory));
    }

    private HBox breadcrumbs(Callback<TreeItem<String>, Button> crumbFactory) {
        int count = 5;
        TreeItem<String> model = Breadcrumbs.buildTreeModel(
                generate(() -> FAKER.science().element(), count).toArray(String[]::new)
        );

        var nextBtn = new Button("Next");
        nextBtn.getStyleClass().add(Styles.ACCENT);

        var breadcrumbs = new Breadcrumbs<>(model);
        breadcrumbs.setSelectedCrumb(getAncestor(model, count / 2));
        if (crumbFactory != null) { breadcrumbs.setCrumbFactory(crumbFactory); }

        nextBtn.setOnAction(e -> {
            TreeItem<String> selected = breadcrumbs.getSelectedCrumb();
            if (selected.getChildren().size() > 0) {
                breadcrumbs.setSelectedCrumb(selected.getChildren().get(0));
            }
        });

        breadcrumbs.selectedCrumbProperty().addListener((obs, old, val) -> {
            if (val != null) {
                nextBtn.setDisable(val.getChildren().isEmpty());
            }
        });

        var box = new HBox(60, nextBtn, breadcrumbs);
        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }

    private <T> TreeItem<T> getAncestor(TreeItem<T> node, int height) {
        var counter = height;
        var current = node;
        while (counter > 0 && current.getParent() != null) {
            current = current.getParent();
            counter--;
        }
        return current;
    }
}
