/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.Breadcrumbs.BreadCrumbItem;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class BreadcrumbsPage extends AbstractPage {

    public static final String NAME = "Breadcrumbs";

    private static final int CRUMB_COUNT = 5;

    @Override
    public String getName() {
        return NAME;
    }

    public BreadcrumbsPage() {
        super();
        setUserContent(new VBox(
            Page.PAGE_VGAP,
            basicSample(),
            customCrumbSample(),
            customDividerSample()
        ));
    }

    private SampleBlock basicSample() {
        return new SampleBlock("Basic", createBreadcrumbs(null, null));
    }

    private SampleBlock customCrumbSample() {
        Callback<BreadCrumbItem<String>, ButtonBase> crumbFactory = crumb -> {
            var btn = new Button(crumb.getValue(), new FontIcon(randomIcon()));
            btn.getStyleClass().add(Styles.FLAT);
            btn.setFocusTraversable(false);
            return btn;
        };

        return new SampleBlock("Flat Button", createBreadcrumbs(crumbFactory, null));
    }

    private SampleBlock customDividerSample() {
        Callback<BreadCrumbItem<String>, ? extends Node> dividerFactory = item -> {
            if (item == null) {
                return new Label("", new FontIcon(Material2AL.HOME));
            }
            return !item.isLast() ? new Label("", new FontIcon(Material2AL.CHEVRON_RIGHT)) : null;
        };

        return new SampleBlock("Custom Divider", createBreadcrumbs(null, dividerFactory));
    }

    private HBox createBreadcrumbs(Callback<BreadCrumbItem<String>, ButtonBase> crumbFactory,
                                   Callback<BreadCrumbItem<String>, ? extends Node> dividerFactory) {
        BreadCrumbItem<String> model = Breadcrumbs.buildTreeModel(
            generate(() -> FAKER.science().element(), CRUMB_COUNT).toArray(String[]::new)
        );

        var nextBtn = new Button("Next");
        nextBtn.getStyleClass().addAll(Styles.ACCENT);

        var breadcrumbs = new Breadcrumbs<>(model);
        breadcrumbs.setSelectedCrumb(getAncestor(model, CRUMB_COUNT / 2));
        if (crumbFactory != null) {
            breadcrumbs.setCrumbFactory(crumbFactory);
        }
        if (dividerFactory != null) {
            breadcrumbs.setDividerFactory(dividerFactory);
        }

        nextBtn.setOnAction(e -> {
            BreadCrumbItem<String> selected = breadcrumbs.getSelectedCrumb();
            if (selected.getChildren().size() > 0) {
                breadcrumbs.setSelectedCrumb((BreadCrumbItem<String>) selected.getChildren().get(0));
            }
        });

        breadcrumbs.selectedCrumbProperty().addListener((obs, old, val) -> {
            if (val != null) {
                nextBtn.setDisable(val.getChildren().isEmpty());
            }
        });

        var box = new HBox(40, nextBtn, breadcrumbs);
        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }

    private <T> BreadCrumbItem<T> getAncestor(BreadCrumbItem<T> node, int height) {
        var counter = height;
        var current = node;
        while (counter > 0 && current.getParent() != null) {
            current = (BreadCrumbItem<T>) current.getParent();
            counter--;
        }
        return current;
    }
}
