/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import atlantafx.sampler.layout.Overlay;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.datafaker.Faker;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static atlantafx.sampler.util.Containers.setScrollConstraints;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;

public abstract class AbstractPage extends BorderPane implements Page {

    protected static final Faker FAKER = new Faker();
    protected static final Random RANDOM = new Random();
    protected static final EventHandler<ActionEvent> PRINT_SOURCE = System.out::println;

    protected VBox userContent;
    protected Overlay overlay;
    protected boolean isRendered = false;

    protected AbstractPage() {
        super();

        getStyleClass().add("page");
        createPageLayout();
    }

    protected void createPageLayout() {
        userContent = new VBox();
        userContent.getStyleClass().add("user-content");

        var userContentWrapper = new StackPane();
        userContentWrapper.getStyleClass().add("wrapper");
        userContentWrapper.getChildren().setAll(userContent);

        var scrollPane = new ScrollPane(userContentWrapper);
        setScrollConstraints(scrollPane, AS_NEEDED, true, AS_NEEDED, true);
        scrollPane.setMaxHeight(10_000);

        setCenter(scrollPane);
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return true;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return true;
    }

    @Override
    public void reset() { }

    protected void layoutChildren() {
        super.layoutChildren();
        if (isRendered) { return; }

        isRendered = true;
        onRendered();
    }

    // Some properties can only be obtained after node placed
    // to the scene graph and here is the place do this.
    protected void onRendered() {
        this.overlay = lookupOverlay();
    }

    protected Overlay lookupOverlay() {
        return getScene() != null && getScene().lookup("." + Overlay.STYLE_CLASS) instanceof Overlay overlay ? overlay : null;
    }

    ///////////////////////////////////////////////////////////////////////////

    protected <T> List<T> generate(Supplier<T> supplier, int count) {
        return Stream.generate(supplier).limit(count).collect(Collectors.toList());
    }

    protected Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }
}
