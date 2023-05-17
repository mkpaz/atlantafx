/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class PaginationPage extends OutlinePage {

    public static final String NAME = "Pagination";

    @Override
    public String getName() {
        return NAME;
    }

    public PaginationPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A [i]Pagination[/i] control is used for navigation between pages of a single content, \
            which has been divided into smaller parts."""
        );
        addSection("Usage", usageExample());
        addSection("Bullet Style", bulletStyleExample());
        addSection("No Arrows", noArrowsExample());
        addSection("Page Info Alignment", pageInfoAlignmentExample());
        addSection("Playground", playground());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var pg = new Pagination(25, 0);
        pg.setMaxPageIndicatorCount(5);
        pg.setPageFactory(index -> {
            var label = new Label("Page #" + (index + 1));
            label.setStyle("-fx-font-size: 2em;");
            return new BorderPane(label);
        });
        //snippet_1:end

        var box = new HBox(pg);
        var description = BBCodeParser.createFormattedText("""
            A pagination control consists of the page content and the page navigation areas. \
            The [i]Page[/i] content area renders and lays out the content according to the \
            application logic. The [i]Page[/i] navigation area contains a prefabricated control \
            to preview a particular part of the content."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox bulletStyleExample() {
        //snippet_2:start
        var pg = new Pagination(25, 0);
        pg.setMaxPageIndicatorCount(5);
        pg.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        pg.setPageFactory(index -> {
            var label = new Label("Page #" + (index + 1));
            label.setStyle("-fx-font-size: 2em;");
            return new BorderPane(label);
        });
        //snippet_2:end

        var box = new HBox(pg);
        var description = BBCodeParser.createFormattedText("""
            The control can be customized to display bullet style indicators by setting \
            the style class [code]STYLE_CLASS_BULLET[/code]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox noArrowsExample() {
        //snippet_3:start
        var pg = new Pagination(25, 0);
        pg.setMaxPageIndicatorCount(5);
        pg.setStyle("-fx-arrows-visible:false;");
        pg.setPageFactory(index -> {
            var label = new Label("Page #" + (index + 1));
            label.setStyle("-fx-font-size: 2em;");
            return new BorderPane(label);
        });
        //snippet_3:end

        var box = new HBox(pg);
        var description = BBCodeParser.createFormattedText("""
            [code]-fx-arrows-visible[/code] can be used to to toggle the visibility of the [i]Next[/i] \
            and [i]Previous[/i] button arrows."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox pageInfoAlignmentExample() {
        //snippet_4:start
        var pg = new Pagination(25, 0);
        pg.setMaxPageIndicatorCount(5);

        // Note this. It's another ancient #javafx-bug:
        // https://bugs.openjdk.org/browse/JDK-8088711
        // Pagination doesn't calculate its size correctly.
        // To work around this issue, you should adjust the pagination
        // width to include page info label manually.
        pg.setPrefWidth(300);

        pg.setPageFactory(index -> {
            var label = new Label("Page #" + (index + 1));
            label.setStyle("-fx-font-size: 2em;");
            return new BorderPane(label);
        });

        var topRadio = new RadioButton("Top");
        topRadio.setUserData("-fx-page-information-alignment:top;");

        var rightRadio = new RadioButton("Right");
        rightRadio.setUserData("-fx-page-information-alignment:right;");

        var bottomRadio = new RadioButton("Bottom");
        bottomRadio.setUserData("-fx-page-information-alignment:bottom;");
        bottomRadio.setSelected(true);

        var leftRadio = new RadioButton("Left");
        leftRadio.setUserData("-fx-page-information-alignment:left;");

        var sideGroup = new ToggleGroup();
        sideGroup.getToggles().setAll(
            topRadio, rightRadio, bottomRadio, leftRadio
        );
        sideGroup.selectedToggleProperty().addListener(
            (obs, old, val) -> pg.setStyle(String.valueOf(val.getUserData()))
        );
        //snippet_4:end

        var toggleBox = new VBox(
            VGAP_10, topRadio, rightRadio, bottomRadio, leftRadio
        );

        var box = new HBox(50, pg, toggleBox);
        box.setAlignment(Pos.TOP_LEFT);
        var description = BBCodeParser.createFormattedText("""
            Page information alignment can be changed via [code]-fx-page-information-alignment[/code] \
            CSS variables. Unfortunately, it's not implemented as an observable property."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playground                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private VBox playground() {
        var pg = new Pagination();
        pg.setCurrentPageIndex(0);
        pg.setPageFactory(index -> {
            var label = new Label("Page #" + (index + 1));
            label.setStyle("-fx-font-size: 3em;");
            return new BorderPane(label);
        });

        // == CONTROLS ==

        var pageCountSpinner = new Spinner<Integer>(0, 50, 25);
        pageCountSpinner.setPrefWidth(120);
        pg.pageCountProperty().bind(pageCountSpinner.valueProperty());

        var visibleCountSpinner = new Spinner<Integer>(3, 10, 5);
        visibleCountSpinner.setPrefWidth(120);
        pg.maxPageIndicatorCountProperty().bind(visibleCountSpinner.valueProperty());

        var bulletToggle = new ToggleSwitch();
        bulletToggle.selectedProperty().addListener(
            (obs, old, val) -> Styles.toggleStyleClass(pg, Pagination.STYLE_CLASS_BULLET)
        );

        var showArrowsToggle = new ToggleSwitch();
        showArrowsToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                pg.setStyle(String.format("-fx-arrows-visible: %s;", val));
            }
        });
        showArrowsToggle.setSelected(true);

        var showPageInfoToggle = new ToggleSwitch();
        showPageInfoToggle.selectedProperty().addListener((obs, old, val) -> {
            if (val != null) {
                pg.setStyle(String.format("-fx-page-information-visible: %s;", val));
            }
        });
        showPageInfoToggle.setSelected(true);

        var disableToggle = new ToggleSwitch();
        pg.disableProperty().bind(disableToggle.selectedProperty());

        var controls = new GridPane();
        controls.setHgap(20);
        controls.setVgap(10);
        controls.setAlignment(Pos.CENTER);

        controls.add(new Label("Page count"), 0, 0);
        controls.add(pageCountSpinner, 1, 0);

        controls.add(new Label("Visible count"), 0, 1);
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

        var description = BBCodeParser.createFormattedText("""
            The playground demonstrates the most important [i]Pagination[/i] features \
            and also serves as an object for monkey testing."""
        );

        var playground = new VBox(VGAP_10, description, pg, new Separator(), controls);
        playground.setPrefHeight(200);

        return playground;
    }
}
