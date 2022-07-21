/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.controls.Spacer;
import atlantafx.sampler.page.AbstractPage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaginationPage extends AbstractPage {

    public static final String NAME = "Pagination";
    private static final int PREF_CONTROL_WIDTH = 120;

    @Override
    public String getName() { return NAME; }

    public PaginationPage() {
        super();
        createView();
    }

    private void createView() {
        var playground = new VBox(10);
        playground.setMinHeight(100);
        playground.setAlignment(Pos.CENTER);

        var pagination = new Pagination();
        pagination.setCurrentPageIndex(1);
        pagination.setPageFactory(index -> {
            var label = new Label("Page #" + (index + 1));
            label.setStyle("-fx-font-size: 3em;");

            var page = new BorderPane();
            page.setCenter(label);

            return page;
        });

        // == CONTROLS ==

        var pageCountSpinner = new Spinner<Integer>(0, 50, 25);
        pageCountSpinner.setPrefWidth(PREF_CONTROL_WIDTH);
        pagination.pageCountProperty().bind(pageCountSpinner.valueProperty());

        var visibleCountSpinner = new Spinner<Integer>(3, 10, 5);
        visibleCountSpinner.setPrefWidth(PREF_CONTROL_WIDTH);
        pagination.maxPageIndicatorCountProperty().bind(visibleCountSpinner.valueProperty());

        var bulletToggle = new ToggleSwitch();
        bulletToggle.selectedProperty().addListener(
                (obs, old, val) -> Styles.toggleStyleClass(pagination, Pagination.STYLE_CLASS_BULLET)
        );

        var showArrowsToggle = new ToggleSwitch();
        showArrowsToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) { pagination.setStyle(String.format("-fx-arrows-visible: %s;", val)); }
        });
        showArrowsToggle.setSelected(true);

        var showPageInfoToggle = new ToggleSwitch();
        showPageInfoToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) { pagination.setStyle(String.format("-fx-page-information-visible: %s;", val)); }
        });
        showPageInfoToggle.setSelected(true);

        var disableToggle = new ToggleSwitch();
        pagination.disableProperty().bind(disableToggle.selectedProperty());

        var controls = new GridPane();
        controls.setHgap(20);
        controls.setVgap(10);

        controls.add(new Label("Page count:"), 0, 0);
        controls.add(pageCountSpinner, 1, 0);

        controls.add(new Label("Visible count:"), 0, 1);
        controls.add(visibleCountSpinner, 1, 1);

        controls.add(new Label("Bullet style"), 3, 0);
        controls.add(bulletToggle, 4, 0);

        controls.add(new Label("Show arrows"), 3, 1);
        controls.add(showArrowsToggle, 4, 1);

        controls.add(new Label("Show info"), 5, 0);
        controls.add(showPageInfoToggle, 6, 0);

        controls.add(new Label("Disable"), 5, 1);
        controls.add(disableToggle, 6, 1);

        // ~

        var separator = new Separator();
        separator.getStyleClass().addAll(Styles.LARGE);

        playground.getChildren().setAll(
                pagination,
                separator,
                new HBox(new Spacer(), controls, new Spacer())
        );

        userContent.getChildren().setAll(playground);
    }
}
