/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.sampler.page.Page;
import java.util.function.Supplier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

// JavaFX Skin API is very complex and almost undocumented. In many cases it's much simpler
// to create a small widget that just do the job than wasting hours to debug control behaviour.
// Consider this as a cookbook of those widgets.
public class WidgetCollectionPage extends BorderPane implements Page {

    public static final String NAME = "Widgets";

    @Override
    public String getName() {
        return NAME;
    }

    private final ListView<Example> toc = new ListView<>();
    private final VBox widgetWrapper = new VBox(PAGE_HGAP);
    private boolean isRendered = false;

    public WidgetCollectionPage() {
        super();
        createView();
    }

    private void createView() {
        widgetWrapper.getStyleClass().add("widget");
        widgetWrapper.setAlignment(Pos.TOP_CENTER);
        widgetWrapper.setFillWidth(false);

        toc.setCellFactory(c -> new TocCell());
        toc.getStyleClass().addAll("toc", Styles.DENSE, Tweaks.EDGE_TO_EDGE);
        toc.getItems().setAll(Example.values());
        toc.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            widgetWrapper.getChildren().setAll(val.getSupplier().get());
        });

        // ~

        setCenter(widgetWrapper);
        setRight(toc);
        BorderPane.setMargin(toc, new Insets(0, 0, 0, PAGE_HGAP));
        getStyleClass().setAll("page", "widget-collection");

        toc.getSelectionModel().selectFirst();
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return false;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return true;
    }

    @Override
    public void reset() {
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (isRendered) {
            return;
        }

        isRendered = true;
        toc.getSelectionModel().selectFirst();
        toc.requestFocus();
    }

    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("ImmutableEnumChecker")
    public enum Example {
        CARD("Card", CardSample::new),
        MESSAGE("Message", MessageSample::new),
        STEPPER("Stepper", StepperSample::new),
        TAG("Tag", TagSample::new);

        private final String name;
        private final Supplier<Pane> supplier;

        Example(String name, Supplier<Pane> supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        public String getName() {
            return name;
        }

        public Supplier<Pane> getSupplier() {
            return supplier;
        }
    }

    private static class TocCell extends ListCell<Example> {

        @Override
        protected void updateItem(Example item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getName());
            }
        }
    }
}
